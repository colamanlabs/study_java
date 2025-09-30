package com.colamanlabs.netty;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class SimplePropertiesManager
{

    /*
getClassLoader().getResourceAsStream() 메소드 를 설명해주세요
ChatGPT의 말:

getClassLoader().getResourceAsStream() 메서드는 클래스패스(classpath)에 있는 리소스 파일을 읽기 위해 InputStream을 반환하는 메서드입니다.

기본 개념

위치: ClassLoader 클래스에 정의됨
리턴타입: InputStream
인자: 문자열(String name) → 리소스의 경로
용도: JAR 내부나 resources 폴더 같은 클래스패스에 포함된 파일을 읽을 때 사용

동작 방식
ClassLoader는 클래스 로딩뿐만 아니라, 리소스 탐색 기능도 제공함.
getResourceAsStream(String name) 호출 시, 클래스패스 상의 리소스를 찾고,
있으면 해당 리소스에 대한 InputStream 반환
없으면 null 반환

반환된 InputStream을 이용해 파일 내용을 읽을 수 있음.
경로 규칙
경로는 클래스패스 기준임.
보통 src/main/resources 아래에 있는 파일은 컴파일 후 target/classes에 복사되어 클래스패스에 포함됨.

getClassLoader().getResourceAsStream("config/app.properties")
→ resources/config/app.properties 파일을 읽음.

경로 앞에 /를 붙이지 않음. (ClassLoader 기준에서는 항상 "절대 경로" 취급)
     */
    public static Properties loadProperties(String fileName) throws Exception
    {
        InputStream in = SimplePropertiesManager.class.getClassLoader().getResourceAsStream(fileName);
        log.debug(String.format("[SimplePropertiesManager/loadProperties] fileName:[%s]", fileName));
        log.debug(String.format("[SimplePropertiesManager/loadProperties] in:[%s]", in));
        Properties props = new Properties();

        try
        {
            props.load(in);
            System.out.println(props.getProperty("host"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error(String.format("[SimplePropertiesManager/loadProperties] error:[%s]", e.getMessage()), e);
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        log.debug(String.format("[SimplePropertiesManager/loadProperties] props:[%s]", props));
        return props;

    }
}
