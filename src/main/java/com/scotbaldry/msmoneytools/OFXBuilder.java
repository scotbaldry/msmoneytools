package com.scotbaldry.msmoneytools;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.scotbaldry.ofxschema.BooleanType;
import com.scotbaldry.ofxschema.Currency;
import com.scotbaldry.ofxschema.CurrencyEnum;
import com.scotbaldry.ofxschema.GeneralSecurityInfo;
import com.scotbaldry.ofxschema.InvestmentAccount;
import com.scotbaldry.ofxschema.InvestmentPosition;
import com.scotbaldry.ofxschema.InvestmentPositionList;
import com.scotbaldry.ofxschema.InvestmentStatementResponse;
import com.scotbaldry.ofxschema.InvestmentStatementResponseMessageSetV1;
import com.scotbaldry.ofxschema.InvestmentStatementTransactionResponse;
import com.scotbaldry.ofxschema.MutualFundInfo;
import com.scotbaldry.ofxschema.MutualFundTypeEnum;
import com.scotbaldry.ofxschema.OFX;
import com.scotbaldry.ofxschema.PositionMutualFund;
import com.scotbaldry.ofxschema.PositionTypeEnum;
import com.scotbaldry.ofxschema.SecurityId;
import com.scotbaldry.ofxschema.SecurityList;
import com.scotbaldry.ofxschema.SecurityListResponseMessageSetV1;
import com.scotbaldry.ofxschema.SeverityEnum;
import com.scotbaldry.ofxschema.SignonResponse;
import com.scotbaldry.ofxschema.SignonResponseMessageSetV1;
import com.scotbaldry.ofxschema.Status;
import com.scotbaldry.ofxschema.SubAccountEnum;

public class OFXBuilder {
    private Date _valuationDate;
    private List<SecurityPrice> _securityPrices;

    public OFXBuilder(Date valuationDate, List<SecurityPrice> securityPrices) {
        _valuationDate = valuationDate;
        _securityPrices = securityPrices;
    }

    public OFX buildOFX() {
        OFX ofx = new OFX();

        SignonResponseMessageSetV1 signOnResponseMsgSet = getSignonResponseMessageSetV1(_valuationDate);

        InvestmentStatementResponseMessageSetV1 investmentStmtResponseMsgSet = new InvestmentStatementResponseMessageSetV1();
        InvestmentStatementTransactionResponse investmentStmtTxnResponse = new InvestmentStatementTransactionResponse();
        investmentStmtTxnResponse.setTRNUID(getUUID());
        Status status1 = new Status();
        status1.setCODE("0");
        status1.setSEVERITY(SeverityEnum.INFO);
        investmentStmtTxnResponse.setSTATUS(status1);

        InvestmentStatementResponse investmentStmtResponse = new InvestmentStatementResponse();
        investmentStmtResponse.setDTASOF(formatDate(_valuationDate));
        investmentStmtResponse.setCURDEF(CurrencyEnum.GBP);
        InvestmentAccount investmentAccount = new InvestmentAccount();
        investmentAccount.setBROKERID("fake.com");
        investmentAccount.setACCTID("0123456789");
        investmentStmtResponse.setINVACCTFROM(investmentAccount);
        investmentStmtTxnResponse.setINVSTMTRS(investmentStmtResponse);
        investmentStmtResponseMsgSet.getINVSTMTTRNRSOrINVSTMTENDTRNRSOrINVMAILTRNRS().add(investmentStmtTxnResponse);

        InvestmentPositionList investmentPositionList = new InvestmentPositionList();
        for (SecurityPrice securityPrice : _securityPrices) {
            PositionMutualFund positionMutualFund = buildPositionMutualFund(securityPrice.getSymbol(),
                                                                            securityPrice.getCurrency(),
                                                                            securityPrice.getPrice(),
                                                                            securityPrice.getDate());
            investmentPositionList.getPOSMFOrPOSSTOCKOrPOSDEBT().add(positionMutualFund);
        }
        investmentStmtResponse.setINVPOSLIST(investmentPositionList);

        SecurityListResponseMessageSetV1 securityListResponseMessageSetV1 = new SecurityListResponseMessageSetV1();
        SecurityList securityList = new SecurityList();

        for (SecurityPrice securityPrice : _securityPrices) {
            MutualFundInfo mutualFundInfo = buildMutualFundInfo(securityPrice.getSymbol(),
                                                                securityPrice.getSecurityName(),
                                                                securityPrice.getCurrency(),
                                                                securityPrice.getPrice(),
                                                                securityPrice.getDate());
            securityList.getMFINFOOrSTOCKINFOOrOPTINFO().add(mutualFundInfo);
        }

        securityListResponseMessageSetV1.setSECLIST(securityList);

        ofx.setINVSTMTMSGSRSV1(investmentStmtResponseMsgSet);
        ofx.setSECLISTMSGSRSV1(securityListResponseMessageSetV1);
        ofx.setSIGNONMSGSRSV1(signOnResponseMsgSet);

        return ofx;
    }

    private MutualFundInfo buildMutualFundInfo(String symbol, String securityName, String ccy, String price, Date date) {
        MutualFundInfo mutualFundInfo = new MutualFundInfo();
        mutualFundInfo.setMFTYPE(MutualFundTypeEnum.OPENEND);
        GeneralSecurityInfo securityInfo = new GeneralSecurityInfo();
        SecurityId securityId = new SecurityId();
        securityId.setUNIQUEID(symbol);
        securityId.setUNIQUEIDTYPE("TICKER");
        securityInfo.setSECID(securityId);
        securityInfo.setSECNAME(securityName);
        securityInfo.setTICKER(symbol);
        securityInfo.setUNITPRICE(price);
        securityInfo.setDTASOF(formatDate(date));
        securityInfo.setMEMO("Price as of date based on closing price");
        Currency currency = new Currency();
        currency.setCURSYM(CurrencyEnum.fromValue(ccy));
        currency.setCURRATE("1.00");
        securityInfo.setCURRENCY(currency);
        mutualFundInfo.setSECINFO(securityInfo);
        return mutualFundInfo;
    }

    private PositionMutualFund buildPositionMutualFund(String symbol, String ccy, String price, Date date) {
        InvestmentPosition investmentPosition = new InvestmentPosition();
        SecurityId securityId = new SecurityId();
        securityId.setUNIQUEID(symbol);
        securityId.setUNIQUEIDTYPE("TICKER");
        investmentPosition.setSECID(securityId);
        investmentPosition.setHELDINACCT(SubAccountEnum.OTHER);
        investmentPosition.setPOSTYPE(PositionTypeEnum.LONG);
        investmentPosition.setUNITS("1.00");
        investmentPosition.setUNITPRICE(price);
        investmentPosition.setMKTVAL("0.00");
        investmentPosition.setDTPRICEASOF(formatDate(date));
        investmentPosition.setMEMO("Price as of date based on closing price");
        Currency currency = new Currency();
        currency.setCURSYM(CurrencyEnum.fromValue(ccy));
        currency.setCURRATE("1.00");
        investmentPosition.setCURRENCY(currency);
        PositionMutualFund positionMutualFund = new PositionMutualFund();
        positionMutualFund.setINVPOS(investmentPosition);
        positionMutualFund.setREINVDIV(BooleanType.Y);
        positionMutualFund.setREINVCG(BooleanType.Y);

        return positionMutualFund;
    }

    private SignonResponseMessageSetV1 getSignonResponseMessageSetV1(Date date) {
        SignonResponseMessageSetV1 signOnResponseMsgSet = new SignonResponseMessageSetV1();
        SignonResponse signonResponse = new SignonResponse();
        Status status = new Status();
        status.setCODE("0");
        status.setSEVERITY(SeverityEnum.INFO);
        status.setMESSAGE("Successful Sign On");
        signonResponse.setSTATUS(status);
        signonResponse.setDTSERVER(formatDate(date));
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

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(date);
    }
}
