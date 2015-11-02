# OkHttpMockSample

* [OkHttpのMockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)を試すサンプルコード
* 社内勉強会向け

## Activity

### RealActivity

* 本物のGitHub APIからリポジトリリストを取得して表示

### MockSuccessActivity

* MockWebServerからリポジトリリストを取得して表示

### MockUnavailableActivity

* 503 Service Unavailableな状態をMockWebServerでエミュレート

## JUnit

* `app/src/androidTest/java/com/hosopy/okhttpmocksample/api/service/GitHubServiceTest.java`
* MockWebServerを活用したAPI Clientのテストコード
