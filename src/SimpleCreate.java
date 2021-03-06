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
        ArrayList cols = new ArrayList();
        cols.add("id");
        cols.add("name");

        OdaDataSetHandle dataSet = createDataSet(eFactory, "DS", "ds", cols, "localization");
        dataSet.setResultSetName("naaeae");


        design.getDataSets().add(dataSet);

        /**/
        //IConnection conn = new JDBCInformixJDBCConnection()
        //IQuery


//        IResultSetMetaData md = new I
//        ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign(md);

        /**/





        // Adding Table
        TableHandle table = eFactory.newTableItem("table", cols.size());
        table.setWidth("100%");
        table.setDataSet(design.findDataSet("ds"));

        // Table Binding
        PropertyHandle computedSet = table.getColumnBindings();
        ComputedColumn cs1 = null;
        System.out.println(dataSet.getResultSetNumber());

        for( int i=0; i < cols.size(); i++){
            cs1 = StructureFactory.createComputedColumn();
            cs1.setName((String)cols.get(i));
            cs1.setExpression("dataSetRow[\"" + (String)cols.get(i) + "\"]");
            computedSet.addItem(cs1);
        }

        // header defining
            RowHandle tableHeader = (RowHandle) table.getHeader().get(0);
            CellHandle h0 = (CellHandle) tableHeader.getCells().get(0);
            h0.getContent().add(createLabel(eFactory, "label1", "Колонка 1"));

            // detail defining
            RowHandle tableDetail = (RowHandle) table.getDetail().get(0);

//        for( int i=0; i < cols.size(); i++){
//            CellHandle c = (CellHandle) tableDetail.getCells( ).get( i );
//            //DataItemHandle data = eFactory.newDataItem( /*"dataSetRow[\"" +*/(String)cols.get(i)/* + "\"]" */);
//            TextDataItem data = eFactory.newTextItem(null)
//            data.setHasExpression(true);
//            data.setContentType(DesignChoiceConstants.TEXT_DATA_CONTENT_TYPE_AUTO);
//            data.setValueExpr("row[\"name\"]");
//            c.getContent( ).add( data );
//            //c.getContent( ).add(createLabel(eFactory, "label1", "Колонка 1"));
//        }


        for( int i=0; i < cols.size(); i++){
            CellHandle c = (CellHandle) tableDetail.getCells( ).get( i );
            DataItemHandle data = eFactory.newDataItem( "data_"+(String)cols.get(i) );
            data.setResultSetColumn( (String)cols.get(i));
            c.getContent( ).add( data );
        }

//        CellHandle c = (CellHandle) tableDetail.getCells( ).get( 0 );
//        TextItemHandle data = eFactory.newTextItem(null);
//        data.setContentType(DesignChoiceConstants.TEXT_CONTENT_TYPE_HTML);
//        data.setHasExpression(false);
//        data.setContent("<H1><VALUE-OF>1</VALUE-OF></H1>");
//        data.setContentKey("a");
//        c.getContent( ).add( data );



        design.getBody().add(table);


        //ExpressionUtil


        // Save the design and close it.
        design.saveAs( "d:/temp/sample1.rptdesign"); //$NON-NLS-1$//$NON-NLS-2$
        design.close( );
        System.out.println("Finished");

        // We're done!
    }
}