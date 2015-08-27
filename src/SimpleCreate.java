import com.ibm.icu.util.ULocale;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;

import java.io.IOException;

/**
 * Simple BIRT Design Engine API (DEAPI) demo.
 */
public class SimpleCreate
{
    public static void main( String[] args )
    {
        try
        {
            buildReport( );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        catch( SemanticException e )
        {
            e.printStackTrace();
        }
    }

    static LabelHandle createLabel(ElementFactory efactory, String name, String text) throws SemanticException {
        LabelHandle l = efactory.newLabel(name);
        l.setText(text);
        return l;
    }

    // This method shows how to build a very simple BIRT report with a
    // minimal set of content: a simple grid with an image and a label.

    static void buildReport() throws IOException, SemanticException
    {
        // Create a session handle. This is used to manage all open designs.
        // Your app need create the session only once.

        //Configure the Engine and start the Platform
        DesignConfig config = new DesignConfig( );

        config.setProperty("BIRT_HOME", "d:/birt_runtime/ReportEngine");
        IDesignEngine engine = null;
        try
        {
            Platform.startup( config );
            IDesignEngineFactory factory = (IDesignEngineFactory) Platform.createFactoryObject( IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY );
            engine = factory.createDesignEngine( config );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }

        SessionHandle session = engine.newSessionHandle( ULocale.ENGLISH ) ;

        // Create a new report design.
        ReportDesignHandle design = session.createDesign( );

        // The element factory creates instances of the various BIRT elements.
        ElementFactory eFactory = design.getElementFactory( );

        // Create a simple master page that describes how the report will appear when printed.
        //
        // Note: The report will fail to load in the BIRT designer unless you create a master page.
        DesignElementHandle element = eFactory.newSimpleMasterPage( "Page Master" );
        design.getMasterPages( ).add( element );

        // Create a grid and add it to the "body" slot of the report design.
        GridHandle grid = eFactory.newGridItem( null, 2 /* cols */, 1 /* row */ );
        design.getBody( ).add(grid);

        // Note: Set the table width to 100% to prevent the label
        // from appearing too narrow in the layout view.
        grid.setWidth( "100%" );

        // Get the first row.
        RowHandle row = (RowHandle) grid.getRows( ).get( 0 );

        // Create an image and add it to the first cell.
        ImageHandle image = eFactory.newImage( null );
        CellHandle cell = (CellHandle) row.getCells( ).get( 0 );
        cell.getContent( ).add(image);
//        image.setURL( "\"urlofimage\"" );

        // Create a label and add it to the second cell.
        LabelHandle label = eFactory.newLabel( null );
        cell = (CellHandle) row.getCells( ).get( 1 );
        cell.getContent( ).add( label );
        label.setText("Hello, world!");


        // Adding DataSource
        OdaDataSourceHandle dataSource = eFactory.newOdaDataSource("DS", "org.eclipse.birt.report.data.oda.jdbc");
        dataSource.setProperty("odaDriverClass", "org.postgresql.Driver");
        dataSource.setProperty("odaURL", "jdbc:postgresql://127.0.0.1:6532/iis");
        dataSource.setProperty("odaUser", "postgres");
        dataSource.setProperty("odaPassword", "postgres");
        design.getDataSources().add(dataSource);

        // Adding DataSet
        OdaDataSetHandle dataSet = eFactory.newOdaDataSet("ds", "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
        dataSet.setDataSource("DS");
        String qry = "Select * from localization";
        dataSet.setQueryText(qry);
        design.getDataSets().add(dataSet);

        // Adding Table
        TableHandle table = eFactory.newTableItem("table", 3);
        table.setWidth("100%");
        table.setDataSet(design.findDataSet("ds"));
            // header defining
            RowHandle tableHeader = (RowHandle) table.getHeader().get(0);
            CellHandle h0 =  (CellHandle) tableHeader.getCells().get(0);
            h0.getContent().add(createLabel(eFactory, "label1", "Колонка 1"));

        design.getBody().add(table);





        // Save the design and close it.
        design.saveAs( "d:/temp/sample1.rptdesign");
        design.close( );
        System.out.println("Finished");

        // We're done!
    }
}