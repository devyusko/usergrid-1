/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.usergrid.persistence.index.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.usergrid.persistence.core.guice.MigrationManagerRule;
import org.apache.usergrid.persistence.core.test.UseModules;
import org.apache.usergrid.persistence.index.EntityIndexFactory;
import org.apache.usergrid.persistence.index.IndexOperationMessage;
import org.apache.usergrid.persistence.index.guice.TestIndexModule;

import com.google.inject.Inject;

import net.jcip.annotations.NotThreadSafe;

import static org.junit.Assert.*;




@RunWith(EsRunner.class)
@UseModules({ TestIndexModule.class })
@NotThreadSafe
public class BufferQueueSQSImplTest {


    @Inject
    @Rule
    public MigrationManagerRule migrationManagerRule;

    @Inject
    private BufferQueueSQSImpl bufferQueueSQS;

    @Inject
    private EsIndexBufferConsumerImpl esIndexBufferConsumer;


    @Before
    public void stop() {
        esIndexBufferConsumer.stop();
    }


    @After
    public void after() {
        esIndexBufferConsumer.start();
    }




    @Test
    public void testMessageIndexing(){

        final Map<String, Object> request1Data  = new HashMap<String, Object>() {{put("test", "testval1");}};
        final IndexRequest indexRequest1 =  new IndexRequest( "testAlias1", "testType1", "testDoc1",request1Data );


        final Map<String, Object> request2Data  = new HashMap<String, Object>() {{put("test", "testval2");}};
        final IndexRequest indexRequest2 =  new IndexRequest( "testAlias2", "testType2", "testDoc2",request2Data );


        //de-index request
        final DeIndexRequest deIndexRequest1 = new DeIndexRequest( new String[]{"index1.1, index1.2"}, "testType3", "testId3" );

        final DeIndexRequest deIndexRequest2 = new DeIndexRequest( new String[]{"index2.1", "index2.1"}, "testType4", "testId4" );




        IndexOperationMessage indexOperationMessage = new IndexOperationMessage();
        indexOperationMessage.addIndexRequest( indexRequest1);
        indexOperationMessage.addIndexRequest( indexRequest2);

        indexOperationMessage.addDeIndexRequest( deIndexRequest1 );
        indexOperationMessage.addDeIndexRequest( deIndexRequest2 );

        bufferQueueSQS.offer( indexOperationMessage );

        //wait for it to send to SQS
        indexOperationMessage.getFuture().get();

        //now get it back

        final List<IndexOperationMessage> ops = bufferQueueSQS.take( 10,  20, TimeUnit.SECONDS );

        assertTrue(ops.size() > 1);

        final IndexOperationMessage returnedOperation = ops.get( 0 );

         //get the operations out

        final Set<IndexRequest> indexRequestSet = returnedOperation.getIndexRequests();

        assertTrue(indexRequestSet.contains(indexRequest1));
        assertTrue(indexRequestSet.contains(indexRequest2));


        final Set<DeIndexRequest> deIndexRequests = returnedOperation.getDeIndexRequests();

        assertTrue( deIndexRequests.contains( deIndexRequest1 ) );
        assertTrue( deIndexRequests.contains( deIndexRequest2 ) );



        //now ack the message

        bufferQueueSQS.ack( ops );

    }




}