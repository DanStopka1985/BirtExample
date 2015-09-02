import com.ibm.icu.util.ULocale;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Simple BIRT Design Engine API (DEAPI) demo.
 */
public class SimpleCreate1
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

    static OdaDataSetHandle createDataSet(ElementFactory eFactory, String dataSourceName, String dataSetName, ArrayList cols, String fromClause) throws SemanticException {
        OdaDataSetHandle dataSet = eFactory.newOdaDataSet(dataSetName, "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
        dataSet.setDataSource(dataSourceName);
        String qry = "select ";
        for( int i=0; i < cols.size(); i++){
            qry += " " + cols.get(i);
            if( i != (cols.size() -1) ){
                qry += ",";
            }
        }
        qry += " from " + fromClause;
        dataSet.setQueryText( qry );
        return dataSet;
    }


    // This method shows how to build a very simple BIRT report with a
    // minimal set of content: a simple grid with an image and a label.

    static void buildReport() throws IOException, SemanticException
    {
        // Create a session handle. This is used to manage all open designs.
        // Your app need create the session only once.

        //Configure the Engine and start the Platform
        DesignConfig config = new DesignConfig( );

        //config.setProperty("BIRT_HOME", "d:/birt_runtime/ReportEngine");
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
        ArrayList cols = new ArrayList();
        cols.add("id");
        cols.add("name");

        OdaDataSetHandle dataSet = createDataSet(eFactory, "DS", "ds", cols, "localization");
        dataSet.setResultSetName("naaeae");


        design.getDataSets().add(dataSet);


//        //adding new grid
//        GridHandle grid1 = eFactory.newGridItem( null, 2 /* cols */, 1 /* row */ );
//        design.getBody( ).add(grid1);
//
//        PropertyHandle computedSet = grid1.getColumnBindings( );
//        ComputedColumn cs1 = null;
//
//
//        grid1.setDataSet(dataSet);
//            cs1 = StructureFactory.createComputedColumn();
//            cs1.setName("name");
//            cs1.setExpression("dataSetRow[\"name\"]");
//            computedSet.addItem(cs1);
//
//        RowHandle row0 = (RowHandle) grid1.getRows( ).get( 0 );
//        CellHandle c0 = (CellHandle) row0.getCells().get(0);
//
//
//        TextItemHandle data = eFactory.newTextItem(null);
//        data.setContentType(DesignChoiceConstants.TEXT_CONTENT_TYPE_HTML);
//        data.setHasExpression(false);
//        data.setContent("<H1>1</H1>");
//        data.setContentKey("a");
//        c0.getContent( ).add( data );


        DataItemHandle data = null;
        data = eFactory.newDataItem("asd");

        ComputedColumn cs1 = null;
        cs1 = StructureFactory.createComputedColumn();
        cs1.setName("data_name");
        cs1.setExpression("1+1");
        cs1.setDataType("integer");


        PropertyHandle computedSet = data.getColumnBindings();
        computedSet.addItem(cs1);
        //computedSet.getContents()


        data.setResultSetColumn("name");


        design.getBody( ).add(data);

//        grid1.setDataSet(dataSet);
//            cs1 = StructureFactory.createComputedColumn();
//            cs1.setName("name");
//            cs1.setExpression("dataSetRow[\"name\"]");
//            computedSet.addItem(cs1);





        // Save the design and close it.
        design.saveAs( "d:/temp/test.rptdesign"); //$NON-NLS-1$//$NON-NLS-2$
        design.close( );
        System.out.println("Finished");

        // We're done!
    }
}