package com.scotbaldry;


import com.scotbaldry.ofxschema.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OFXBuilder {
    private List<SecurityPrice> _securityPrices;

    public OFXBuilder(List<SecurityPrice> securityPrices) {
        _securityPrices = securityPrices;
    }

    public OFX buildOFX() {
        OFX ofx = new OFX();

        SignonResponseMessageSetV1 signOnResponseMsgSet = getSignonResponseMessageSetV1();

        InvestmentStatementResponseMessageSetV1 investmentStmtResponseMsgSet = new InvestmentStatementResponseMessageSetV1();
        InvestmentStatementTransactionResponse investmentStmtTxnResponse = new InvestmentStatementTransactionResponse();
        investmentStmtTxnResponse.setTRNUID(getUUID());
        Status status1 = new Status();
        status1.setCODE("0");
        status1.setSEVERITY(SeverityEnum.INFO);
        investmentStmtTxnResponse.setSTATUS(status1);

        InvestmentStatementResponse investmentStmtResponse = new InvestmentStatementResponse();
        investmentStmtResponse.setDTASOF(getDate());
        investmentStmtResponse.setCURDEF(CurrencyEnum.GBP);
        InvestmentAccount investmentAccount = new InvestmentAccount();
        investmentAccount.setBROKERID("fake.com");
        investmentAccount.setACCTID("0123456789");
        investmentStmtResponse.setINVACCTFROM(investmentAccount);
        investmentStmtTxnResponse.setINVSTMTRS(investmentStmtResponse);
        investmentStmtResponseMsgSet.getINVSTMTTRNRSOrINVSTMTENDTRNRSOrINVMAILTRNRS().add(investmentStmtTxnResponse);

        InvestmentPositionList investmentPositionList = new InvestmentPositionList();
        for (SecurityPrice securityPrice : _securityPrices) {
            PositionStock positionStock = buildPositionStock(securityPrice.getSymbol(), securityPrice.getPrice());
            investmentPositionList.getPOSMFOrPOSSTOCKOrPOSDEBT().add(positionStock);
        }
        investmentStmtResponse.setINVPOSLIST(investmentPositionList);

        SecurityListResponseMessageSetV1 securityListResponseMessageSetV1 = new SecurityListResponseMessageSetV1();
        SecurityList securityList = new SecurityList();

        for (SecurityPrice securityPrice : _securityPrices) {
            StockInfo stockInfo = buildStockInfo(securityPrice.getSymbol(),
                                                 securityPrice.getSecurityName(),
                                                 securityPrice.getPrice());
            securityList.getMFINFOOrSTOCKINFOOrOPTINFO().add(stockInfo);
        }

        securityListResponseMessageSetV1.setSECLIST(securityList);

        ofx.setINVSTMTMSGSRSV1(investmentStmtResponseMsgSet);
        ofx.setSECLISTMSGSRSV1(securityListResponseMessageSetV1);
        ofx.setSIGNONMSGSRSV1(signOnResponseMsgSet);

        return ofx;
    }

    private StockInfo buildStockInfo(String symbol, String securityName, String price) {
        StockInfo stockInfo = new StockInfo();
        GeneralSecurityInfo securityInfo = new GeneralSecurityInfo();
        SecurityId securityId = new SecurityId();
        securityId.setUNIQUEID(symbol);
        securityId.setUNIQUEIDTYPE("TICKER");
        securityInfo.setSECID(securityId);
        securityInfo.setSECNAME(securityName);
        securityInfo.setTICKER(symbol);
        securityInfo.setUNITPRICE(price);
        securityInfo.setDTASOF(getDate());
        securityInfo.setMEMO("Price as of date based on closing price");
        stockInfo.setSECINFO(securityInfo);
        return stockInfo;
    }

    private PositionStock buildPositionStock(String symbol, String price) {
        InvestmentPosition investmentPosition = new InvestmentPosition();
        SecurityId securityId = new SecurityId();
        securityId.setUNIQUEID(symbol);
        securityId.setUNIQUEIDTYPE("TICKER");
        investmentPosition.setSECID(securityId);
        investmentPosition.setHELDINACCT(SubAccountEnum.OTHER);
        investmentPosition.setPOSTYPE(PositionTypeEnum.LONG);
        investmentPosition.setUNITS("0");
        investmentPosition.setUNITPRICE(price);
        investmentPosition.setMKTVAL("0.00");
        investmentPosition.setDTPRICEASOF(getDate());
        investmentPosition.setMEMO("Price as of date based on closing price");
        PositionStock positionStock = new PositionStock();
        positionStock.setINVPOS(investmentPosition);
        return positionStock;
    }

    private SignonResponseMessageSetV1 getSignonResponseMessageSetV1() {
        SignonResponseMessageSetV1 signOnResponseMsgSet = new SignonResponseMessageSetV1();
        SignonResponse signonResponse = new SignonResponse();
        Status status = new Status();
        status.setCODE("0");
        status.setSEVERITY(SeverityEnum.INFO);
        status.setMESSAGE("Sucessful Sign On");
        signonResponse.setSTATUS(status);
        signonResponse.setDTSERVER(getDate());
        signonResponse.setLANGUAGE("ENG");
        signOnResponseMsgSet.setSONRS(signonResponse);
        return signOnResponseMsgSet;
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private String getDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormatter.format(date);
    }
}
