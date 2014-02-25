package org.apache.usergrid.management.export;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.usergrid.batch.JobExecution;
import org.apache.usergrid.batch.job.OnlyOnceJob;
import org.apache.usergrid.management.ExportInfo;
import org.apache.usergrid.persistence.entities.JobData;


/**
 * you could make an enum here, that contains the state info look at scotts code and emulate that to see wha tyou can
 * return in the json object
 */
@Component("exportJob")
public class ExportJob extends OnlyOnceJob {
    public static final String EXPORT_ID = "exportId";
    private static final Logger logger = LoggerFactory.getLogger( ExportJob.class );

    @Autowired
    ExportService exportService;

    public ExportJob() {
        logger.info( "ExportJob created " + this );
    }


    @Override
    public void doJob( JobExecution jobExecution ) throws Exception {
        logger.info( "execute ExportJob {}", jobExecution );

        UUID exportId = ( UUID ) jobExecution.getJobData().getProperty( EXPORT_ID );
        //as long as I have the entity UUID I should be able to find it from anywhere right?


        ExportInfo config = null;
        JobData jobData = jobExecution.getJobData();
        if ( jobData == null ) {
            logger.error( "jobData cannot be null" );
            return;
        }
        config = ( ExportInfo ) jobData.getProperty( "exportInfo" );
        if ( config == null ) {
            logger.error( "Export information cannot be null" );
            return;
        }

        jobExecution.heartbeat();
        try {
            exportService.doExport( config, jobExecution );
        }
        catch ( Exception e ) {
            logger.error( "Export Service failed to complete job" );
            return;
        }

        logger.info( "executed ExportJob process completed" );
    }


    @Override
    protected long getDelay( JobExecution jobExecution ) throws Exception {
        //return arbitrary number
        return 100;
    }


    @Autowired
    public void setExportService( final ExportService exportService ) {
        this.exportService = exportService;
    }
}
