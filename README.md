#  kcg：kaadog code generator

​	

# 它能做什么

​		支持 Mysql、Oracle、DB2、Microsoft SQL Server、GaussDB、PostgreSQL、MongoDB 数据仓储中的数据结构获取，依托于数据结构使用 freemarker 模板快速生成代码

## 数据仓储支持情况

| Mysql | Oracle | DB2    | Microsoft SQL Server | GaussDB | PostgreSQL | MongoDB |
| ----- | ------ | ------ | -------------------- | ------- | ---------- | ------- |
| 支持  | 适配中 | 计划中 | 支持                 | 支持    | 计划中     | 计划中  |

# kcg 主要功能

- 多数据源支持

  可以指定多个不同数据仓储的数据源

- 多模板路径支持

  支持文件夹、jar 文件、zip 文件、gz 文件

- 全局自定义属性内容

  当每一个模板需要使用相同的属性内容时，可以使用全局属性内容，全局属性将传递调到每一个模板中，使用 freemarker 语法进行读取

- 转换函数配置能力

  在进行表结构获取后，可以自主配置需要转换的内置函数进行表名、字段名等一系列的转换，如果内置函数满足不了需要，可以自定义函数

- 生成文件支持下载功能

  当执行生成时可以使用生成并下载功能，它将提供一个压缩文件并下载到本地

# 开始使用

## Step 1： 获取工具

​		kcg 是一个 **spring boot** 项目，要求具备 **jdk1.8+** 环境。

### 方式一：

​	进入下载页面，[【下载】](https://github.com/cwmchen/kcg/releases) ，选择所需的版本，下载对应的压缩文件，解压后进行 `Step 2`

### 方式二：

​	下载源代码自行打包，进行`Step 2`

#### 	windows

​			cmd 命令执行器：`mvn clean package install & mvn -f kcg-dashboard/pom.xml -P release clean package install -U`

​			PowerShell 命令执行器:	`mvn clean package install ; mvn -f kcg-dashboard/pom.xml -P release clean package install -U`

#### 	linux

​			`mvn clean package install && mvn -f kcg-dashboard/pom.xml -P release clean package install -U  `

> 下载源代码，执行 maven 打包，要求具备 maven 3.5+、jdk 1.8+ 环境。

## Step 2：工具配置

​	kcg 是一个 **spring boot** 项目，采用 yaml 文件进行工具启动配置，可配置的内容比较丰富，可以满足日常代码生成所需的大部分自定义内容，具体参考 [相关配置](#相关配置)

## Step 3：配置模板

​	kcg 内置模板本身内置丰富的模板，可以使用内置模板，也可以通过使用内置模板修改为自己的业务模板（建议），如果有能力也可以自定义业务模板，模板语法参考[FreeMarker 在线手册](http://freemarker.foofun.cn/) 。

> 模板编辑完成后需要放置在配置项 `kaadog.kcg.template.template-folder` 对应的路径下



## Step 4：启动项目

### 方式一：

​	下载发行版本解压直接执行启动

- windows 双击运行  `bin/run.cmd`
- linux  运行  `bin/run.sh`

### 方式二：

​	下载源代码，执行 maven 打包，进入 `kcg-dashboard/target/kcg-dashboard/kcg-dashboard`

- windows 双击运行  `bin/run.cmd`
- linux  运行  `bin/run.sh`

## Step 5：访问生成地址

​	dashboard 地址：`http://127.0.0.1:1666/index.html`

# 相关配置

配置采用 `kaadog.kcg` 前缀，主配置项说明：

| 参数            | 描述     | 默认值 |
| :-------------- | :------- | :----- |
| project-version | 项目版本 | 无     |
| project-name    | 项目名称 | 无     |
| module-name     | 模块名称 | 无     |

​		`kaadog.kcg.template` 模板相关配置

| 参数                            | 描述                                                         | 默认值               |
| :------------------------------ | :----------------------------------------------------------- | :------------------- |
| template-folder                 | 模板存放的文件夹，可以使用绝对路径、相对路径、classpath:、jar 文件、zip 文件、gz文件<br />1、绝对路径使用程序可以访问到的磁盘地址<br />2、相对路径一般使用../../ 的形式，相对的程序运行的目录<br />3、classpath: 程序可以访问到的类路径，如：`classpath:template` ，会获取到当前类路径可访问到的template内所有的文件夹与文件<br />4、jar 文件指定jar文件地址，可以使用  `!` 指定使用哪个文件夹作为模板文件夹，如：`xxx.jar!template`，获取当前  jar  中 `template`  文件夹中的模板<br />5、zip文件、gz 文件同 jar 文件处理一致 |                      |
| out-root-folder                 | 生成文件输出目录（目录必须存在）                             | 用户目录+wokr-folder |
| template-file-suffix            | 模板后缀，非此后缀不会进行模板读取，直接复制文件到输出目录   | .flt                 |
| template-file-encoding          | 模板文件编码                                                 | UTF-8                |
| wokr-folder                     | 进行模板相关操作的工作文件夹                                 | 用户目录+".kcg"      |
| temp-template-folder-retain-day | 工作空间中生成的模板保留的天数                               | 1                    |

​		`kaadog.kcg.properties`，自定义属性内容全局可使用，会传递调每一个模板中，使用 key=value 形式指定

​		`kaadog.kcg.data-source-configurations`，数据源配置，可以指定多个数据源，配置项说明：

| 参数        | 描述                                                         | 默认值      |
| :---------- | :----------------------------------------------------------- | :---------- |
| url         | 数据库连接字符串                                             | 无          |
| username    | 用户名                                                       | 无          |
| password    | 密码                                                         | 无          |
| catalog     | 数据库catalog，如果数据库支持并且存在需要配置                | 无          |
| schema      | 数据库schema，如果数据库支持并且存在需要配置，GaussDB、oracle需要 | 无          |
| properties  | 数据库自定义属性内容，使用key=value形式指定                  | 无          |
| types       | 获取结构时可以获取的类型，支持 TABLE、VIEW                   | TABLE、VIEW |
| table-names | 可以是表名也可以是视图名称，这些表或视图来自数据源配置，按照顺序加载，后加载的会覆盖之前加载的，如果不指定会默认生成所有 | 无          |

​		`kaadog.kcg.transformFunction`，数据转换接口配置，可以指定开启的转换函数，并进行部分函数参数配置，配置项说明：

| 参数                                            | 描述                                                         | 默认值                          |
| :---------------------------------------------- | :----------------------------------------------------------- | :------------------------------ |
| class-name-camel-case-table-function.enabled    | 启用类名使用驼峰命名函数配置                                 | true                            |
| class-name-camel-case-table-function.searchStrs | 搜索的字符串，按照顺序搜索，设定时要注意字符串顺序           | ["t_", "_t_", "t-", "-t-", "-"] |
| column-name-lower-case-column-function.enabled  | 启用列名称转换为小写函数配置                                 | true                            |
| data-type-lower-case-column-function.enabled    | 启用数据类型转换为小写函数配置                               | true                            |
| field-name-camel-case-column-function.enabled   | 列的属性名称使用驼峰命名函数配置                             | true                            |
| field-name-lower-case-column-function.enabled   | 列的属性名称转换为小写函数配置                               | true                            |
| gauss-db-comment-table-function.enabled         | GaussDB 表备注信息获取函数配置，前提条件是数据源配置使用了 gaussDB | true                            |
| table-name-lower-case-table-function.enabled    | 表名称转换为小写函数配置                                     | true                            |

​		`kaadog.kcg.dialect.types`， 方言配置，实现数据库字段类型与特点语言类型的映射关系，如果不指定数据库字段映射关系默认生成为 java String 类型（应为此工具使用 Java 开发），配置项说明：

| 参数       | 描述                                                         | 默认值 |
| :--------- | :----------------------------------------------------------- | :----- |
| data-type  | 数据库类型                                                   | 无     |
| field-type | 字段类型                                                     | 无     |
| class-name | 字段类型所使用的类名，如果指定会进行 import（非内置类型需要指定） | 无     |
| value      | 默认值                                                       | 无     |

​		`kaadog.kcg.dialect.types` 默认值优先级最低，可以进行配置替换，如下为默认值：

| data-type | field-type    | class-name              | value               |
| --------- | ------------- | ----------------------- | ------------------- |
| varchar   | String        |                         |                     |
| varchar2  | String        |                         |                     |
| double    | double        |                         |                     |
| number    | double        |                         |                     |
| boolean   | Boolean       |                         | Boolean.TRUE        |
| integer   | int           |                         |                     |
| int       | int           |                         |                     |
| clob      | String        |                         |                     |
| char      | String        |                         |                     |
| datetime  | LocalDateTime | java.time.LocalDateTime | LocalDateTime.now() |
| date      | LocalDateTime | java.time.LocalDateTime | LocalDateTime.now() |
| longtext  | String        |                         |                     |
| timestamp | LocalDateTime | java.time.LocalDateTime | LocalDateTime.now() |

# roadmap

- 完善数据仓储适配
- 开发 dashboard 仪表盘
  - 支持数据源手动维护
  - 支持动态选择要生成的表
  - 支持可以上传模板文件
  - 支持自定义表结构
  - 支持可以在线编辑模板功能

# 待处理问题

