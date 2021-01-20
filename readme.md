# gitpodでpostgresのDockerの起動のしかた  

1. Dockerファイルとymlファイルを準備する
`.gitpod.Dockerfile`と`.gitpod.yml`を用意する  
※gp init でも作成化  

2. ファイルを書き換える  
 - `.gitpod.Dockerfile`
 ```
 FROM gitpod/workspace-postgres

 USER gitpod
 
 RUN sudo apt update && \
    sudo rm -rf /var/lib/apt/lists/*
 ```

 - `.gitpod.yml`  
 ```
image:
  file: .gitpod.Dockerfile

tasks:
  - init: 'echo "TODO: Replace with init/build command"'
    command: 'echo "TODO: Replace with command to start project"'
 ```

3. Workspaceをリビルドする。  
`gitpod.io/#prebuild/`をWorkspaceのURLに付け加えて開きなおすとプレビルドを手動で実行することができる  
例：`gitpod.io/#prebuild/https://github.com/[ユーザー名]/[リポジトリ名]`  
※リビルドには時間かかります。  

4. リビルド後、Workspaceを開いてpostgresの状態確認をする  
Workspaceを開くとpostgresが起動されている状態のはずなので、  
postgresにログインして`select 1`してみる  


5. 参考URL  
 - Docker構成  
 https://www.gitpod.io/docs/config-docker/  

 - プレビルドの手動実行  
 https://www.gitpod.io/docs/prebuilds/  

 - Gitpodで開発するときのアレコレをまとめておく  
 https://qiita.com/sterashima78/items/fca961e285355715d466  

 - Gitpod で MySQL を勉強する
 https://devlights.hatenablog.com/entry/2021/01/11/031642


# postgresへの接続
0. 下記のURLを参考にPostgres用に読み替えながら進めていく  
https://spring.pleiades.io/guides/gs/accessing-data-mysql/

1. pluginの準備  
Spring Web, Spring Data JPA, Postgresのドライバーを依存関係で準備する  
 →Spring Initializrで準備すると良い  
  https://start.spring.io/  

  必要なもの
  ・Spring Web
  ・Spring Data JPA
  ・PostgreSQL Driver
  
2. DBの作成  
Postgresにログインして下記のようなコマンドを打っていきDB作成、User作成、権限付与していく  
```
~ $ psql
postgres=# create database mydb;
postgres=# create role springuser with login password 'password';
postgres=# GRANT ALL ON ALL TABLES IN SCHEMA public TO springuser;
```

3. Postgresのポート確認する  
デフォルトでは`5432`  
```
postgres=# select setting from pg_settings where name = 'port';
 setting 
---------
 5432
(1 row)
```

4. 各種ソースを準備する。  
 - application.propertiesを準備する  
  `spring.datasource.url`は`jdbc:postgresql://localhost:[postgresのポート]/[作成したDB]`の形にする
  ```
  spring.jpa.hibernate.ddl-auto=update
  spring.datasource.driver-class-name=org.postgresql.Driver
  spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
  spring.datasource.username=springuser
  spring.datasource.password=password  
  ```
  
 - モデルとリポジトリ、コントローラーを作成する。  
  上記URLを参考に作る。  
  ※userテーブルは作れないので別名でソースを作る

5. ビルド＆実行する
 ```
 ./gradlew bootRun
 ~~~~~~~~~~~
 <==========---> 80% EXECUTING [15m 29s]
 > :bootRun
 ```
 上記の状態で、別Terminalを開きApiを叩いていき下記のようなレスポンスがあればOK  
 ```
 $ curl localhost:8080/demo/add -d name=First -d email=someemail@someemailprovider.com
 Saved
 $ curl 'localhost:8080/demo/all'
 [{"id":1,"name":"First","email":"someemail@someemailprovider.com"}] 
 ```

6. テーブルの中身を確認する  
 下記のようになってればOK
 ```
 $ psql -U springuser -d mydb
 mydb=> \dt
            List of relations
  Schema |   Name    | Type  |   Owner    
 --------+-----------+-------+------------
  public | test_user | table | springuser
 (1 rows)

 mydb=> \d test_user
                     Table "public.test_user"
  Column |          Type          | Collation | Nullable | Default 
 --------+------------------------+-----------+----------+---------
  id     | integer                |           | not null | 
  email  | character varying(255) |           |          | 
  name   | character varying(255) |           |          | 
 Indexes:
     "test_user_pkey" PRIMARY KEY, btree (id)

 mydb=> select * from test_user;
  id |              email              | name  
 ----+---------------------------------+-------
   3 | someemail@someemailprovider.com | First
 (1 row)
 ```

 以上