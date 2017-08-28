/**
 * 创建日期:  2017年08月28日 11:28
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.handler;

import com.yangqiang.net.netty.msg.Login;
import com.yangqiang.net.netty.msg.Login.ReqLoginMessage;
import com.yangqiang.net.netty.msg.Login.ResLoginMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨 强
 */
@Slf4j
public class ReqLoginHandler extends AbstractMsgHander<ReqLoginMessage> {
    @Override
    public void handMessage(ReqLoginMessage msg) {
        log.info("收到登录请求[{},{}]", msg.getAccount(), msg.getPassword());

        ResLoginMessage.Builder builder = ResLoginMessage.newBuilder();
        builder.setLoginResult(Login.LoginResult.LOGON_RESULT_SUCCESS);
        ResLoginMessage resLoginMessage = builder.build();
        session.write(resLoginMessage);
    }
}
