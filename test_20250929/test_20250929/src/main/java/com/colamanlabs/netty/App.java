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

        String host = prop.getProperty("server.host", "0.0.0.0");
        int port;
        try
        {
            port = Integer.parseInt(prop.getProperty("server.port", "8080"));
        }
        catch (NumberFormatException e)
        {
            port = 8080;
        }

        int workerThreadCount;
        try
        {
            workerThreadCount = Integer.parseInt(prop.getProperty("server.worker_thread_count", "10"));
        }
        catch (NumberFormatException e)
        {
            workerThreadCount = 10;
        }

        log.info(String.format("[App/main] Starting Netty Echo Server on %s:%d \t workerThreadCount:[%d] ", host, port, workerThreadCount));

        /*
        보통 서버 생성시 가장 중요한 설정값은
        IP, PORT, 워커스레드 수 이다.
        ServerSocket 꺼내는 쓰레드는 리슨포트 1개 이면 여러개를 둘 필요가 없다.
        빨리 꺼내고 워커쓰레드에게 전달하고 다시 다음 백로그큐에 있는 것을 꺼내야 한다.
         */
        EchoServer server = new EchoServer(host, port, workerThreadCount);
        server.startAndBlock();
    }
}
