package com.indra.actions;

import com.indra.models.DataExcelModels;
import com.indra.pages.PortabilityPostActivationPage;
import com.jcraft.jsch.JSchException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PortabilityPostActivationActions extends PortabilityPostActivationPage {
    DataExcelModels dataExcelModels = new DataExcelModels();
    DatabasePortInActions databasePortInActions = new DatabasePortInActions();
    UninstallCBSServicesActions servicesActions = new UninstallCBSServicesActions();
    ShellConnections shellConnections = new ShellConnections();

    public PortabilityPostActivationActions(WebDriver driver) {
        super(driver);
    }

    public void validateLinesBd(String msisdn,String msi) throws SQLException {
        databasePortInActions.cleanLinesMsisdn(msisdn);
        databasePortInActions.cleanLinesMsi(msi);
    }

    public String validateTransctionBd(String msisdn) throws SQLException {
        return databasePortInActions.executePortabilityTransactionStatus(msisdn);
    }

    public void aceptNitBd(String msisdn) throws SQLException {
        databasePortInActions.executePortabilityNip(msisdn);
    }

    public int consultNipBd(String nip) throws SQLException {
        String resultNip = databasePortInActions.executeSelectNip(nip);
        //System.out.println(databasePortInActions.executeSelectNip(nip).length());
        return resultNip.length();
    }

    public void executePortabilityReceptBd(String msisdn) throws SQLException {
        databasePortInActions.executePortabilityRecept(msisdn);
    }

    public void executeUpdatePortIdBd(String msisdn) throws SQLException {
        databasePortInActions.executeUpdatePortId(portId(msisdn), msisdn);
    }

    public void executePortIdBd(String msisdn) throws SQLException {
        databasePortInActions.executePortId(portId(msisdn));
    }

    public List<String> executePortabilityTransactionBd(String msisdn) throws SQLException {
        return databasePortInActions.executePortabilityTransaction(msisdn);
    }

    public void executeWindowPortabilityBd(String msisdn) throws SQLException {
        databasePortInActions.executeWindowPortability(msisdn);
    }

    public void solicitudNip(String msisdnPort) throws SQLException {
        getPreventa().click();
        getPortabilidadNumerica().click();
        getSolicitudes().click();
        getSolicitudNip().click();
        WebElement iframe = getDriver().findElement(By.id("iframe"));
        getDriver().switchTo().frame(iframe);
        enter(msisdnPort).into(getInputMsisdn());
        getBtnSolicitar().click();

        if (getMessage().isVisible()== true){
            getMessage().waitUntilPresent();

            System.out.println("*****************************************************************************");
            System.out.println(getMessage().getText());
            System.out.println("*****************************************************************************");

        }
        else {
            WebElement soliNip = getDriver().findElement(By.xpath("//span[@class='iceMsgsError messageError']"));

            System.out.println("*****************************************************************************");
            System.out.println(soliNip.getText());
            System.out.println("*****************************************************************************");
        }
    }

    public String portId(String msisdn){
        String nip = msisdn.substring(5,10);
        // logica si esta sumar 1 al nip
        // nip = String.valueOf(Integer.valueOf(nip) + 1);
        // repertir consulta
        String portId= "000022011082401"+nip;
        return portId;
    }

    public String nip(String nip) throws SQLException {

        while(consultNipBd(nip) > 0){
            nip = String.valueOf(Integer.valueOf(nip) + 1);
            consultNipBd(nip);
        }
        return nip;
    }

    public void preWindow(String msisdn) throws SQLException {
        executePortabilityReceptBd(msisdn);
        validateTransctionBd(msisdn);
        executePortIdBd(msisdn);
        executeUpdatePortIdBd(msisdn);
        executePortIdBd(msisdn);
    }

    public void initialRute(String msisdnPort, String msiPort) throws SQLException {
        validateLinesBd(msisdnPort,msiPort);
        consultSingleScreen(msisdnPort);
        validateTransctionBd(msisdnPort);
        switchToDefaultContent();
        solicitudNip(msisdnPort);
        validateTransctionBd(msisdnPort);
        aceptNitBd(msisdnPort);
        validateTransctionBd(msisdnPort);
        MatcherAssert.assertThat("el status es PIN_REQUEST_ACEPTADO",
                validateTransctionBd(msisdnPort),Matchers.equalTo("PIN_REQUEST_ACEPTADO"));
        System.out.println();
    }

    public void switchToDefaultContent(){
        getDriver().switchTo().defaultContent();
    }

    public void initialPortability(){
        getDriver().switchTo().defaultContent();
        getSale().click();
        getDropdownActivation().click();
        getDropdownPay().click();
        getActivator().click();
        WebElement iframe = getDriver().findElement(By.id("iframe"));
        getDriver().switchTo().frame(iframe);
        getDropdownActivator().click();
        getPortabilityPospaid().click();
    }

    public void customerInformation(String vendedor,String cliente)  {
        //enter("10960370").into(getVendor());
        enter(vendedor).into(getVendor());
        getButtonId().click();
        getDocumentType().click();
        //enter("667299000").into(getDocumentCC());
        enter(cliente).into(getDocumentCC());
        enter("2000").into(getDocumentExpedicion());
        getBtnContinue().click();
    }

    public void activationPortability(String msisdnPort, String msisdn, String msi) throws SQLException {
        String nip = dataExcelModels.getMsisdnPort().substring(5,10);
        //System.out.println(consultNipBd(nip));
        //System.out.println(nip(nip));
        getTypeTel().click();
        getPospago().click();
        enter(nip(nip)).into(getNip());
        getTypeSol().click();
        getTypeSol1().click();
        enter(msisdnPort).into(getMsisdnPort());
        windowsScrolldown();
        waitABit(1000);
        clickCheckScheduledDate();
        clickInputCalendar();
        selectNextBusinessDayFromCalendar();
        waitABit(2000);
        enter(msi).into(getMsi());
        enter(msisdn).into(getMsisdn());
        getSimSola().click();
        getSimSola1().click();
        getPlan().click();
        getPlanType().click();
        windowsScrolldown();
        waitABit(2000);
        WebElement continuar = getDriver().findElement(By.name("ActivacionesForm:btnContinuarActivacionVenta"));
        continuar.click();
        waitABit(5000);

        WebElement faildDayCalendar = getDriver().findElement(By.id("ActivacionesForm:idFechaActivacionPortabilidadMessage"));
        if(faildDayCalendar.isDisplayed()){
            clickInputCalendar();
            selectNextBusinessDayFromCalendarHoliday();
            windowsScrolldown();
            waitABit(2000);
            WebElement continuar2 = getDriver().findElement(By.name("ActivacionesForm:btnContinuarActivacionVenta"));
            continuar2.click();
            waitABit(5000);
        }

    }

    public  void demographicInformation(){
        enter("Salazar londonio").into(getDistrict());
        getDropdownDeparment().click();
        getDeparment().click();
        getDropdownCity().click();
        getCity().click();
        enter("3222345678").into(getPhone());
        enter("3222345679").into(getAlternatePhone());
        enter("pruebaAutoma@gmail.com").into(getMail());
        getDate().click();
        getChooseDate().click();
        getMonth().click();
        getChooseYear().click();
        getChooseYear().click();
        getYear().click();
        getDateOk().click();
        getDay().click();
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollBy(0,420)"); //Scroll vertically down by 1000 pixels
        getElectronicBill().click();
        getContinueDemo().click();
        //getContinueSale().click();
        waitABit(15000);
        //getConfirm().click();
        WebElement confirmar = getDriver().findElement(By.xpath("//*[@id='popupConfirmacionDatos:confirmarDatos']"));
        confirmar.click();
        getActivationDetails().waitUntilPresent();

        WebElement codigo = getDriver().findElement(By.xpath("//div[@id='errorForm:linkPanel:content']/table/tbody/tr"));
        WebElement descripcion = getDriver().findElement(By.xpath("//div[@id='errorForm:linkPanel:content']/table/tbody/tr[2]"));
        String cod = codigo.getText();
        String desc = descripcion.getText();

        System.out.println("*****************************************************Codigo y Descripcion***********************************************************************************");
        System.out.println("\n\n"+cod+"\n"+desc+"\n\n");
        System.out.println("*****************************************************************************************************************************************");


        WebElement title = getDriver().findElement(By.className("tituloPagina"));
        MatcherAssert.assertThat("La activacion fue exitosa",title.getText(), Matchers.equalTo("ACTIVACION EXITOSA"));
    }

    public void windowsScrolldown(){
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollBy(0,520)");
    }


    public void clickCheckScheduledDate(){
        getCheckFechaCalendarizada().click();
    }

    public void clickInputCalendar(){
        getInputcalendario().click();
    }


    public void selectNextBusinessDayFromCalendar(){
        int currentDay=0;// se usa para indicar cuando llego al dia actual del calendario
        List<WebElement> dias = getDriver().findElements(By.xpath("//td[@onmouseover]")); // almacena todos los dias presentes del calendario
        // ciclo para recorrer la lista de dias del calendario
        for(WebElement dia :dias){
            //si el contador currentDay es igual 1 y es el dia seguiente le hace click (solo selecciona los dias habiles)
            if(currentDay==1 && dia.getAttribute("class").equals("rf-cal-c-cnt-overflow rf-cal-c rf-cal-btn") )
            {
                System.out.println("selecciono este d??a "+ dia.getText());
                dia.click();
                break;
            }
            // si el dia del calendario es igual al dia presente hace el contador currentDay igual a 1.
            if(dia.getAttribute("class").equals("rf-cal-c-cnt-overflow rf-cal-c rf-cal-today rf-cal-btn")){
                currentDay=1;
            }

        }
    }

    public void selectNextBusinessDayFromCalendarHoliday(){
        int currentDay=0;// se usa para indicar cuando llego al dia actual del calendario

        List<WebElement> dias = getDriver().findElements(By.xpath("//td[@onmouseover]")); // almacena todos los dias presentes del calendario
        // ciclo para recorrer la lista de dias del calendario
        for(WebElement dia :dias){
            //si el contador currentDay es igual 1 y es el dia seguiente le hace click (solo selecciona los dias habiles)
            //rf-cal-c-cnt-overflow rf-cal-c rf-cal-sel
            if(currentDay==1 && dia.getAttribute("class").equals("rf-cal-c-cnt-overflow rf-cal-c rf-cal-btn"))
            {
                //System.out.println("selecciono este d??a "+ dia.getText());
                dia.click();
                break;
            }
            // si el dia del calendario es igual al dia presente hace el contador currentDay igual a 1.
            if(dia.getAttribute("class").equals("rf-cal-c-cnt-overflow rf-cal-c rf-cal-sel")){
                currentDay=1;
            }
        }
    }

    public void consultSingleScreen(String msisdn){
        getDriver().switchTo().defaultContent();
        getConsult().click();
        getConsultPos().click();
        getConsultIntegral().click();
        getCosultaPantallaUnica().click();
        WebElement iframe = getDriver().findElement(By.id("iframe"));
        getDriver().switchTo().frame(iframe);
        enter(msisdn).into(getMsisdn2());
        getSearchButton().click();
        getGeneralCustomerInformation().waitUntilPresent();


        WebElement plan = getDriver().findElement(By.xpath("//span[@class='iceOutTxt errorText']"));

        MatcherAssert.assertThat("la informacion",
                plan.getText(),Matchers.containsString("No se encontr") );
    }

    public void consultSingleScreen1(String msisdn){
        getDriver().switchTo().defaultContent();
        getConsult().click();
        //getConsultPos().click();
        //getConsultIntegral().click();
        getCosultaPantallaUnica().click();
        WebElement iframe = getDriver().findElement(By.id("iframe"));
        getDriver().switchTo().frame(iframe);
        enter(msisdn).into(getMsisdn2());
        getSearchButton().click();
        waitABit(1000);
        getGeneralCustomerInformation().waitUntilPresent();

        WebElement plan = getDriver().findElement(By.id("j_id135:j_id161"));
        MatcherAssert.assertThat("el plan es prepago",
                plan.getText(),Matchers.containsString("Plan Tigo Prepago") );

        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollBy(0,520)");
        System.out.println("hizo la validaci??n del plan ");

        getHlrImpre().click();
        System.out.println("pesta??a activaciones");

        getHlr().waitUntilClickable();
        getHlr().click();
        waitABit(1000);
        js.executeScript("window.scrollBy(0,820)");

        WebElement hrl = getDriver().findElement(By.xpath("//div[@class='icePnlClpsblCnt']//textarea[1]"));
        MatcherAssert.assertThat("el hrl es ",
                hrl.getText(),Matchers.containsString("Operation is successful") );

    }

    public void validateLineTemporal(String msisdn) throws SQLException {
        consultSingleScreen1(msisdn);
        validateTransctionBd(msisdn);
    }

    public void validateLineTemporal1(String msisdn) throws SQLException {
        consultSingleScreen2(msisdn);
        validateTransctionBd(msisdn);
    }


    public void consultSingleScreen2(String msisdn){
        getDriver().switchTo().defaultContent();
        getConsult().click();
        getConsultPos().click();
        getConsultIntegral().click();
        getCosultaPantallaUnica().click();
        WebElement iframe = getDriver().findElement(By.id("iframe"));
        getDriver().switchTo().frame(iframe);
        enter(msisdn).into(getMsisdn2());
        getSearchButton().click();
        waitABit(1000);
        getGeneralCustomerInformation().waitUntilPresent();
        WebElement plan = getDriver().findElement(By.id("j_id135:j_id157"));

        MatcherAssert.assertThat("el plan es pospago",
                plan.getText(),Matchers.containsString("Pospago 5.") );

        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollBy(0,420)"); //Scroll vertically down by 1000 pixels
        getHlr().click();
        WebElement hrl = getDriver().findElement(By.id("j_id473:j_id477"));
        MatcherAssert.assertThat("el hrl es ",
                plan.getText(),Matchers.containsString("Operation is successful") );
    }

    public void portabilityRequestSoapUI(String msisdn) throws SQLException {
        String response = servicesActions.portabilidad(executePortabilityTransactionBd(msisdn).get(0),
                executePortabilityTransactionBd(msisdn).get(1),
                executePortabilityTransactionBd(msisdn).get(2),
                executePortabilityTransactionBd(msisdn).get(3),
                executePortabilityTransactionBd(msisdn).get(4),
                dataExcelModels.getPortabilitySoapUI());
        //System.out.println("Response--->"+response);
        MatcherAssert.assertThat("la respuesta del servicio es O",
                servicesActions.extractResponseInformation(response,"return"),Matchers.equalTo("0"));
    }

    public void window(String msisdn) throws SQLException {
        executeWindowPortabilityBd(msisdn);
        portabilityRequestSoapUI(msisdn);
        executeWindowPortabilityBd(msisdn);
    }

    public void adviserKeyGeneration() throws IOException, IllegalAccessException, JSchException{
        shellConnections.connectionSSH(dataExcelModels.getHostSSH(),dataExcelModels.getUserSSh(),dataExcelModels.getPasswordSSH());
        shellConnections.executeCommand("cd && cd  /home/app/Stand_Alone_Process/PortabilidadStandAloneProcess/bin && sh ./PortabilidadStandAloneProcess.sh PORTINPROCESS");
        shellConnections.disconnect();
    }
}
