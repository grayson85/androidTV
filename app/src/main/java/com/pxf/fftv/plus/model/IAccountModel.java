package com.pxf.fftv.plus.model;

import com.pxf.fftv.plus.model.account.Account;
import com.pxf.fftv.plus.model.account.CardCodeResult;
import com.pxf.fftv.plus.model.account.LoginResult;
import com.pxf.fftv.plus.model.account.RegisterResult;

public interface IAccountModel {

    public RegisterResult register(Account account);

    public LoginResult login(Account account);

    public CardCodeResult verifyCardCode(String username, String cardCode);
}
