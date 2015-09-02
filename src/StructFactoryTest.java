
import com.ibm.icu.util.ULocale;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.*;
import org.eclipse.birt.report.model.elements.JointDataSet;
import org.eclipse.birt.report.model.elements.ReportDesign;
import org.eclipse.birt.report.model.elements.ReportItem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple BIRT Design Engine API (DEAPI) demo.
 */

public class StructFactoryTest
{
    ReportDesignHandle designHandle = null;
    ElementFactory designFactory = null;
    StructureFactory structFactory = null;

    public static void main( String[] args )
    {
        try
        {
            StructFactoryTest de = new StructFactoryTest();
            de.buildReport();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( SemanticException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void buildDataSource( ) throws SemanticException
    {

        OdaDataSourceHandle dsHandle = designFactory.newOdaDataSource(
                "DS", "org.eclipse.birt.report.data.oda.jdbc" );
        dsHandle.setProperty( "odaDriverClass", "org.postgresql.Driver" );
        dsHandle.setProperty( "odaURL", "jdbc:postgresql://127.0.0.1:6532/iis" );
        dsHandle.setProperty( "odaUser", "postgres" );
        dsHandle.setProperty( "odaPassword", "postgres" );

        designHandle.getDataSources( ).add( dsHandle );

    }

    void buildDataSet( ) throws SemanticException
    {

        OdaDataSetHandle dsHandle = designFactory.newOdaDataSet( "ds",
                "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet" );
        dsHandle.setDataSource( "DS" );
        String qry = "Select * from localization";

        dsHandle.setQueryText( qry );

        addFilterCondition( dsHandle );

        designHandle.getDataSets( ).add( dsHandle );



    }

    void buildJointDataSet( ) throws SemanticException
    {
        OdaDataSetHandle dsHandle1 = designFactory.newOdaDataSet( "ds1",
                "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet" );
        dsHandle1.setDataSource( "DS" );
        String qry1 = "Select * from localization";

        dsHandle1.setQueryText( qry1 );


        OdaDataSetHandle dsHandle2 = designFactory.newOdaDataSet( "ds2",
                "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet" );
        dsHandle2.setDataSource( "DS" );
        String qry2 = "Select * from localization";

        dsHandle2.setQueryText( qry2 );

        JointDataSetHandle jds = designFactory.newJointDataSet("test");

        designHandle.getDataSets( ).add( dsHandle1 );
        designHandle.getDataSets( ).add( dsHandle2 );

        jds.addDataSet("ds1");
        jds.addDataSet("ds2");

        String leftExpression = "dataSetRow[\"ID\"]";
        String rightExpression = "dataSetRow[\"ID\"]";
        JoinCondition condition = StructureFactory.createJoinCondition( );
        condition.setJoinType( DesignChoiceConstants.JOIN_TYPE_LEFT_OUT );
        condition.setOperator( DesignChoiceConstants.JOIN_OPERATOR_EQALS );
        condition.setLeftDataSet( "ds1" ); //$NON-NLS-1$
        condition.setRightDataSet( "ds2" ); //$NON-NLS-1$
        condition.setLeftExpression( leftExpression ); //$NON-NLS-1$
        condition.setRightExpression( rightExpression ); //$NON-NLS-1$

        PropertyHandle conditionHandle = jds
                .getPropertyHandle( JointDataSet.JOIN_CONDITONS_PROP );
        conditionHandle.addItem( condition );

        designHandle.getDataSets( ).add( jds );


    }

    void addMapRule(TableHandle th){
        try{


            MapRule mr = structFactory.createMapRule();
            mr.setTestExpression("row[\"name\"]");
            mr.setOperator(DesignChoiceConstants.MAP_OPERATOR_EQ);
            mr.setValue1("0");
            mr.setDisplay("N/A");

            PropertyHandle ph = th.getPropertyHandle(StyleHandle.MAP_RULES_PROP);
            ph.addItem(mr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void addVisRule(ReportElementHandle rh){
        try{
            HideRule hr = structFactory.createHideRule();
            hr.setFormat("pdf");
            hr.setExpression("true");

            PropertyHandle ph = rh.getPropertyHandle(ReportItem.VISIBILITY_PROP);
            ph.addItem(hr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void addBottomBorder(ReportElementHandle rh){
        try{


            rh.setProperty(StyleHandle.BORDER_BOTTOM_COLOR_PROP, "#000000");
            rh.setProperty(StyleHandle.BORDER_BOTTOM_STYLE_PROP, "solid");
            rh.setProperty(StyleHandle.BORDER_BOTTOM_WIDTH_PROP, "2px");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void addHighLightRule(RowHandle th){
        try{
            HighlightRule hr = structFactory.createHighlightRule();

            hr.setOperator(DesignChoiceConstants.MAP_OPERATOR_GT);
            hr.setTestExpression("row[\"name\"]");
            hr.setValue1("100000");
            hr.setProperty(HighlightRule.BACKGROUND_COLOR_MEMBER, "blue");

            PropertyHandle ph = th.getPropertyHandle(StyleHandle.HIGHLIGHT_RULES_PROP);

            ph.addItem(hr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void addSortKey(TableHandle th){
        try{
            SortKey sk = structFactory.createSortKey();
            //sk.setKey("row[\"CustomerName\"]");
            sk.setDirection(DesignChoiceConstants.SORT_DIRECTION_ASC);
            sk.setKey("if( params[\"srt\"].value){ if( params[\"srt\"].value == 'a' ){	row[\"CustomerName\"]; }else{ row[\"CustomerCity\"];}}");


            PropertyHandle ph = th.getPropertyHandle(TableHandle.SORT_PROP);
            ph.addItem(sk);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void modSortKey(TableHandle th){
        try{
            SortKeyHandle sk;
            PropertyHandle ph = th.getPropertyHandle(TableHandle.SORT_PROP);
            //get number or iterate
            sk = (SortKeyHandle)ph.get(0);
            sk.setDirection(DesignChoiceConstants.SORT_DIRECTION_DESC);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void addFilterCondition(OdaDataSetHandle dh){
        try{
            FilterCondition fc = structFactory.createFilterCond();
            fc.setExpr("row[\"name\"]");
            fc.setOperator(DesignChoiceConstants.MAP_OPERATOR_EQ);
            fc.setValue1("'USA'");

            dh.addFilter(fc);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void addFilterCondition(TableHandle th){
        try{

            FilterCondition fc = structFactory.createFilterCond();
            fc.setExpr("row[\"name\"]");
            fc.setOperator(DesignChoiceConstants.MAP_OPERATOR_EQ);
            fc.setValue1("'USA'");

            PropertyHandle ph = th.getPropertyHandle(TableHandle.FILTER_PROP);

            ph.addItem(fc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void addHyperlink(LabelHandle lh){
        try{
            Action ac = structFactory.createAction();

            ActionHandle actionHandle = lh.setAction( ac );
            //actionHandle.setURI("'http://www.google.com'");
            actionHandle.setLinkType(DesignChoiceConstants.ACTION_LINK_TYPE_DRILL_THROUGH);
            actionHandle.setReportName("c:/test/xyz.rptdesign");
            actionHandle.setTargetFileType("report-design");
            actionHandle.setTargetWindow("_blank");
            actionHandle.getMember("paramBindings");
            ParamBinding pb = structFactory.createParamBinding();
            pb.setParamName("order");
            pb.setExpression("row[\"name\"]");
            actionHandle.addParamBinding(pb);
			/*
            <structure name="action">
            <property name="linkType">drill-through</property>
            <property name="reportName">detail.rptdesign</property>
            <property name="targetWindow">_blank</property>
            <property name="targetFileType">report-design</property>
            <list-property name="paramBindings">
                <structure>
                    <property name="paramName">order</property>
                    <expression name="expression">row["ORDERNUMBER"]</expression>
                </structure>
            </list-property>
        </structure>
        */




        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void addToc(DataItemHandle dh){
        try{
            TOC myToc = structFactory.createTOC("row[\"name\"]");

            dh.addTOC(myToc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void addImage(){
        try{
            EmbeddedImage image = structFactory.createEmbeddedImage( );
            image.setType( DesignChoiceConstants.IMAGE_TYPE_IMAGE_JPEG );
            image.setData( load( "logo3.jpg" ) );
            image.setName( "mylogo" );

            designHandle.addImage( image );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public byte[] load( String fileName ) throws IOException
    {
        InputStream is = null;

        is = new BufferedInputStream( this.getClass( ).getResourceAsStream(
                fileName ) );
        byte data[] = null;
        if ( is != null )
        {
            try
            {
                data = new byte[is.available( )];
                is.read( data );
            }
            catch ( IOException e1 )
            {
                throw e1;
            }
        }
        return data;
    }
    void addScript(ReportDesignHandle rh){
        try{
            IncludeScript is = structFactory.createIncludeScript();
            is.setFileName("test.js");
            PropertyHandle ph = rh.getPropertyHandle(ReportDesign.INCLUDE_SCRIPTS_PROP);
            ph.addItem(is);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void buildReport() throws IOException, SemanticException
    {

        DesignConfig config = new DesignConfig( );
        config.setBIRTHome("d:/birt-runtime/ReportEngine");
        IDesignEngine engine = null;
        try{
            Platform.startup( config );
            IDesignEngineFactory factory = (IDesignEngineFactory) Platform
                    .createFactoryObject( IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY );
            engine = factory.createDesignEngine( config );

        }catch( Exception ex){
            ex.printStackTrace();
        }


        SessionHandle session = engine.newSessionHandle( ULocale.ENGLISH ) ;

        ReportDesignHandle design = null;



        try{
            //open a design or a template
            designHandle = session.createDesign();
            addScript( designHandle);
            designFactory = designHandle.getElementFactory( );

            buildDataSource();
            buildDataSet();
            //buildJointDataSet();

            TableHandle table = designFactory.newTableItem( "table", 3 );
            table.setWidth( "100%" );
            table.setDataSet( designHandle.findDataSet( "ds" ) );

            PropertyHandle computedSet = table.getColumnBindings( );
            ComputedColumn  cs1, cs2, cs3, cs4;


            cs1 = StructureFactory.createComputedColumn();
            cs1.setName("CustomerName");
            cs1.setExpression("dataSetRow[\"name\"]");
            computedSet.addItem(cs1);
            cs2 = StructureFactory.createComputedColumn();
            cs2.setName("CustomerCity");
            cs2.setExpression("dataSetRow[\"name\"]");
            //cs2.setDataType(dataType)
            computedSet.addItem(cs2);
            cs3 = StructureFactory.createComputedColumn();
            cs3.setName("CustomerCountry");
            cs3.setExpression("dataSetRow[\"name\"]");
            computedSet.addItem(cs3);
            cs4 = StructureFactory.createComputedColumn();
            cs4.setName("CustomerCreditLimit");
            cs4.setExpression("dataSetRow[\"name\"]");
            computedSet.addItem(cs4);


            // table header
            RowHandle tableheader = (RowHandle) table.getHeader( ).get( 0 );

            ColumnHandle ch = (ColumnHandle)table.getColumns().get(0);
            ch.setProperty("width", "50%");

            LabelHandle label1 = designFactory.newLabel("Label1" );
            label1.setOnRender("var x = 3;");
            addBottomBorder(label1);
            label1.setText("Customer");
            CellHandle cell = (CellHandle) tableheader.getCells( ).get( 0 );

            cell.getContent( ).add( label1 );
            LabelHandle label2 = designFactory.newLabel("Label2" );
            label2.setText("City");
            cell = (CellHandle) tableheader.getCells( ).get( 1 );
            cell.getContent( ).add( label2 );
            LabelHandle label3 = designFactory.newLabel("Label3" );
            label3.setText("Credit Limit");
            cell = (CellHandle) tableheader.getCells( ).get( 2 );

            cell.getContent( ).add( label3 );


            // table detail
            RowHandle tabledetail = (RowHandle) table.getDetail( ).get( 0 );


            cell = (CellHandle) tabledetail.getCells( ).get( 0 );
            DataItemHandle data = designFactory.newDataItem( "data1" );
            data.setResultSetColumn("name");

            addToc( data );


            cell.getContent( ).add( data );
            cell = (CellHandle) tabledetail.getCells( ).get( 1 );
            data = designFactory.newDataItem( "data2" );
            data.setResultSetColumn("name");
            cell.getContent( ).add( data );
            cell = (CellHandle) tabledetail.getCells( ).get( 2 );
            data = designFactory.newDataItem( "data3" );
            data.setResultSetColumn("name");
            cell.getContent( ).add( data );

            addHyperlink(label1);
            addMapRule(table);
            addHighLightRule(tabledetail);
            addSortKey(table);
            modSortKey(table);
            addFilterCondition(table);
            addImage();

            RowHandle tablefooter = (RowHandle) table.getFooter().get( 0 );
            cell = (CellHandle) tablefooter.getCells( ).get( 0 );

//            ImageHandle image1 = designFactory.newImage( "Logo" );
//
//            image1.setImageName( "mylogo" );
//            addVisRule( image1 );
//            cell.getContent( ).add( image1 );

//            ScalarParameterHandle sph = designFactory.newScalarParameter("srt");
//            sph.setIsRequired(false);
//
//            sph.setValueType(DesignChoiceConstants.PARAM_VALUE_TYPE_STATIC);
//            sph.setDataType(DesignChoiceConstants.PARAM_TYPE_STRING);
//            designHandle.getParameters().add(sph);

            designHandle.getBody( ).add( table );

            // Save the design and close it.
            designHandle.saveAs("d:/temp/test.rptdesign" );
            designHandle.close( );
            Platform.shutdown();
            System.out.println("Finished");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
