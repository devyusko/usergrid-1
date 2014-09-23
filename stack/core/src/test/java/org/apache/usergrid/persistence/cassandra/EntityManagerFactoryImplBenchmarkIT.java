package org.apache.usergrid.persistence.cassandra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.usergrid.AbstractBenchmarkIT;
import org.apache.usergrid.persistence.DynamicEntity;
import org.apache.usergrid.persistence.Entity;
import org.apache.usergrid.persistence.EntityManager;
import org.apache.usergrid.persistence.EntityRef;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class EntityManagerFactoryImplBenchmarkIT extends AbstractBenchmarkIT {

	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 10)
    @Test
    public void simpleCreate() throws Exception {


        final EntityManager em = app.getEntityManager();

        final Map<String, Object> addToCollectionEntity = new HashMap<>();

        addToCollectionEntity.put( "key1", 1000 );
        addToCollectionEntity.put( "key2", 2000 );
        addToCollectionEntity.put( "key3", "Some value" );

        addToCollectionEntity.put( "key", "test" );

        final Entity created = em.create( "testType", addToCollectionEntity );
        Thread.sleep(1);
        Thread.yield();
    }


	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 10)
    @Test
    public void addToMultipleOwners() throws Exception {


        final EntityManager em = app.getEntityManager();

        final DynamicEntity toCreate = new DynamicEntity();
        toCreate.setType( "toCreate" );


        //now create the first entity
        final Entity owner1 = em.create( "1owner", new HashMap<String, Object>() {{
            put( "key", "owner1" );
        }} );
        final Entity owner2 = em.create( "2owner", new HashMap<String, Object>() {{
            put( "key", "owner2" );
        }} );


        final Map<String, Object> addToCollectionEntity = new HashMap<>();
        addToCollectionEntity.put( "key1", 1000 );
        addToCollectionEntity.put( "key2", 2000 );
        addToCollectionEntity.put( "key3", "Some value" );

        final List<EntityRef> owners = Arrays.asList( ( EntityRef ) owner1, ( EntityRef ) owner2 );

        addToCollectionEntity.put( "key", "test" );

        final Entity created = em.create( "testType", addToCollectionEntity );

        em.addToCollections( owners, "test", created );
        Thread.sleep(1);
        Thread.yield();
    }
}
