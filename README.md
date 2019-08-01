Http Client Library
=====

Http Client Library(이하 HCL)는 okhttp3와 gson을 활용한 Http 통신 라이브러리입니다.  
**Java 7, Android SdkVersion 16 버전**부터 지원합니다.  

HCL은 다음을 지원합니다.
* Annotation을 통한 Custom Service Interface 제공.
    * GET, POST, PUT, DELETE, HEAD의 모든 http method 지원.
    * Query, Path Parameter 지원.
    * RequestBody/ResponseBody Object에 대한 Converting 지원.
    * FormUrlEncoded 지원.
* 동적 URL 지원.
* 동기, 비동기 통신 지원.
* https, http 지원.
* Interceptor 지원.
    * Network Interceptor
    * Application Interceptor
* Request Cancel 지원.
* Request Timeout 지원.
    * call, connect, read, write  

  

Dependency 추가
-----
### Gradle
프로젝트에서 루트 수준의 build.gradle에 Maven URL을 추가해주세요.

```groovy
allprojects {
    repositories {
        maven { url "https://dl.bintray.com/naver/HttpClientLib" }
    }
}
```

그리고 앱 수준의 build.gradle에 다음과 같이 dependency를 추가해주세요.

```groovy
dependencies {
    implementation 'com.naver.httpclientlib:HttpClientLib:0.1.0'
}
```

### Maven
다음과 같이 Repository와 Dependency를 지정해주세요.

```xml
<repositories>
  ...
  <repository>
    <id>HttpClientLib</id>
    <url>https://dl.bintray.com/naver/HttpClientLib</url>
  </repository>
</repositories>

<dependencies>
  ...
  <dependency>
    <groupId>com.naver.httpclientlib</groupId>
    <artifactId>HttpClientLib</artifactId>
    <version>0.1.0</version>
    <type>pom</type>
  </dependency>
</dependencies>
```



자세한 내용은 [Wiki][1]를 참고하세요.



[1]: https://oss.navercorp.com/da-intern-2019-1h/http-client-sdk/wiki
