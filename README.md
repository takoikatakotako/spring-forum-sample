## SpringBoot で簡単な掲示板を作成する

SpringBootを使って簡単な掲示板を作成するサンプルです。

### プロジェクトの作成

まず最初 [Spring Initializr](https://start.spring.io/) でプロジェクトを作成します。
Gradle + Java11 のプロジェクトを作成し、Dependencies には Spring Boot Actuator, Spring Web, Thymeleaf を追加します。

![init](image/init.png)


### DBの作成

次にデータベースを用意します。
今回は Docker を使って MySQL を用意します。
以下のような `docker-compose.yml` と `forum.ddl` を作成します。

``` 
version: "3.7"
services:
  forum-db:
    image: mysql:8.0.20
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: forum
    command: mysqld --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    ports:
      - 3306:3306
    volumes:
      - ./forum.ddl:/docker-entrypoint-initdb.d/0.sql
```

```
set
    character_set_client = utf8mb4;
set
    character_set_connection = utf8mb4;

create table post(
    post_id bigint not null auto_increment,
    nickname varchar(63) not null,
    message varchar(1023) not null,
    post_datetime datetime not null,
    primary key(post_id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_bin comment '投稿情報';
```

`docker-compose.yml` があるディレクトリで `docker-compose up -d` コマンドを実行すると MySQLサーバーを起動させることができます。
MySQLサーバーを止めるときは `docker-compose down` コマンドを実行します。データも一緒に消えるので注意が必要です。

MySQLサーバーへログインする場合は以下のコマンドでログインすることができますが、ローカルに MySQL を入れる必要があります。

```
mysql --host 127.0.0.1 --port 3306 -u root -p  # password
```


### APIを作成

投稿APIと投稿取得APIを作成します。 
Spring Initializr を IntelliJ で開き、`build.gradle` に以下を追加します。

```
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'mysql:mysql-connector-java'
implementation group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: '2.11.1'
compileOnly 'org.projectlombok:lombok:1.18.12'
annotationProcessor 'org.projectlombok:lombok:1.18.12'
```

`application.properties` にDBの接続情報を追加します。

```
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/forum
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.jpa.database=MYSQL
```

[Post.java](https://github.com/takoikatakotako/spring-forum-sample/blob/master/src/main/java/com/swiswiswift/forum/Post.java) を作成します。
`Post` クラスはエンティティクラスと呼ばれるデータの入れ物となるクラスで、データベースのテーブルの一行に対応するように作成します。`Post.java` の `Post` クラスは  `forum.ddl` の `post` テーブルに対応するように作成しています。

[PostRepository.java](https://github.com/takoikatakotako/spring-forum-sample/blob/master/src/main/java/com/swiswiswift/forum/PostRepository.java) を作成します。
`PostRepository` インターフェースは、DBへのCRUD（登録、読み取り、変更、削除）を自動で実装してくれるインターフェースです。

[PostRequestBean.java](https://github.com/takoikatakotako/spring-forum-sample/blob/master/src/main/java/com/swiswiswift/forum/PostRequestBean.java)を追加します。

`ForumController` を作成します

```
package com.swiswiswift.forum;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class ForumController {

    private final PostRepository postRepository;

    @ResponseBody
    @RequestMapping(value = "/api/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ArrayList<Post> getPostList(HttpServletResponse response) {
        return new ArrayList<>(postRepository.findAll());
    }

    @ResponseBody
    @RequestMapping(value = "/api/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String addPost(@RequestBody PostRequestBean postRequestBean, HttpServletResponse response) {
        Post post = new Post();
        post.setNickname(postRequestBean.getNickname());
        post.setMessage(postRequestBean.getMessage());
        post.setPostDatetime(LocalDateTime.now());
        postRepository.save(post);
        response.setStatus(HttpServletResponse.SC_OK);
        return "Success";
    }
}
```

投稿一覧を取得

```
curl -X GET -H "Content-Type: application/json" localhost:8080/api/list | jq
```

投稿一覧を取得


```
curl -X POST -H "Content-Type: application/json" -d '{"nickname":"Kabigon", "message":"ZZZZZZZZZZZ"}' localhost:8080/api/add
```


[index.html](https://github.com/takoikatakotako/spring-forum-sample/blob/master/src/main/resources/templates/index.html) を追加します。


[ForumController.java](https://github.com/takoikatakotako/spring-forum-sample/blob/master/src/main/java/com/swiswiswift/forum/ForumController.java) にエンドポイントを追加。


```
@RequestMapping(value = "", method = RequestMethod.GET)
public String healthCheck(Model model) {
    ArrayList<Post> posts = new ArrayList<>(postRepository.findAll());
    model.addAttribute("posts", posts);
    return "index";
}
```


完了！


### jar ファイルを作成

プロジェクトのルートディレクトリで `./gradlew build` コマンドを実行し、プロジェクトをビルドします。
ビルドに成功すると `build/libs/forum-0.0.1-SNAPSHOT.jar` に `jar` ファイルが生成されます。
`jar` ファイルは単体で動かすことでき、以下のようなコマンドで実行することができます。

```
java -Duser.timezone=JST -jar forum-0.0.1-SNAPSHOT.jar
```

このコマンドを実行した状態でブラウザを開き、 `http://localhost:8080/` にアクセスするとアプリケーションが起動していることがわかります。
このコマンドにはオプションを追加でき、たとえば以下のオプションで 80番ポートでアプリケーションを起動します。

```
java -Duser.timezone=JST -Dserver.port=80 -jar forum-0.0.1-SNAPSHOT.jar
```

DBのパスワードなどを上書きできます。

```
java -Duser.timezone=JST \
    -jar forum-0.0.1-SNAPSHOT.jar \
    --spring.datasource.url=jdbc:mysql://127.0.0.1:3306/forum \
    --spring.datasource.username=root \
    --spring.datasource.password=password
```


### jar ファイルで作った SpringBoot と Conoha のDBを接続させる

[Conoha](https://www.conoha.jp/) でデーターベースを借ります。

`n99qs_forum` というデータベースを作成し、そのデーターベースにアクセスできる `n99qs_forum_user` というユーザーを作成します。

![データベースを作成](image/db0.png)
![データベースを作成](image/db1.png)

借りたデータベースにローカル環境からアクセスします。

```
mysql --host public.n99qs.tyo1.database-hosting.conoha.io --port 3306 -u n99qs_forum_user -p
```

データベースを選択します。

```
use use n99qs_forum;
```

`post` テーブルを作成します。`forum.ddl` の中身を実行します。

```
set
    character_set_client = utf8mb4;
set
    character_set_connection = utf8mb4;

create table post(
    post_id bigint not null auto_increment,
    nickname varchar(63) not null,
    message varchar(1023) not null,
    post_datetime datetime not null,
    primary key(post_id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_bin comment '投稿情報';
```

テーブルを作成したら `jar` ファイルに Conohaのデータベースの情報を入力します。`USER_PASSWORD` には設定した値を入力してください。

```
java -Duser.timezone=JST \
    -jar forum-0.0.1-SNAPSHOT.jar \
    --spring.datasource.url=jdbc:mysql://public.n99qs.tyo1.database-hosting.conoha.io:3306/n99qs_forum \
    --spring.datasource.username=n99qs_forum_user \
    --spring.datasource.password=USER_PASSWORD
```

ブラウザから `localhost:8080` にアクセスして正常にアプリケーションが表示されるはずです。


### ConohaVPS に 作ったアプリケーションをデプロイする

次に ConohaVPS にアプリケーションを設定します。
Conoha で VPS をレンタルします。今回は `Ubuntu` の `20.x` を使用しました。
サーバーを立ち上げてしばらくしたらログインします。
`118.27.108.173` は今回借りたサーバーのIPアドレスです。

```
ssh root@118.27.108.173
```

ログインに成功したらアップデートを行います。

```
apt-get -y update
apt-get -y upgrade
```

`Java` をインストールします。

```
apt install openjdk-11-jre-headless
java --version
```

インストールが完了したら一度サーバーからログアウトし、scp コマンドで `jar` ファイルをサーバーに転送します。

```
scp forum-0.0.1-SNAPSHOT.jar root@118.27.108.173:/opt
```

転送したサーバーに再度ログインし、アプリケーションを80番ポートで立ち上げます。

```
java -Duser.timezone=JST \
     -Dserver.port=80 \
     -jar forum-0.0.1-SNAPSHOT.jar \
     --spring.datasource.url=jdbc:mysql://public.n99qs.tyo1.database-hosting.conoha.io:3306/n99qs_forum \
     --spring.datasource.username=n99qs_forum_user \
     --spring.datasource.password=123SDF234sd
```

アプリケーションをバックグラウンドで動かします。

```
command + z
bg 1
```

ブラウザにIPアドレスを入力し、アプリケーションが表示されるはずです。
