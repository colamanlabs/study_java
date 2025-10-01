package com.colamanlabs.netty;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 *
 * 1.
 * 외부 의존 lib 는 최소화, 가능한 java 기본 API 로 구현한다.
 *
 */
@Slf4j
public class App
{
    public static void main(String[] args) throws Exception
    {
        log.debug(String.format("[App/main] user.dir:[%s]", System.getProperty("user.dir")));

        Properties prop = SimplePropertiesManager.loadProperties("config.properties");
        log.debug(String.format("[App/main] prop:[%s]", prop));

        String host = prop.getProperty("host", "0.0.0.0");
        int port;
        try
        {
            port = Integer.parseInt(prop.getProperty("port", "8080"));
        }
        catch (NumberFormatException e)
        {
            port = 8080;
        }
        log.info(String.format("[App/main] Starting Netty Echo Server on %s:%d", host, port));
        EchoServer server = new EchoServer(host, port);
        server.startAndBlock();
    }
}
