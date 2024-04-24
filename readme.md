# MPVP(maven) 

  中文 / [English](readme_en.md)


    Maven项目版本插件，可用于版本快速傻瓜式升级及项目版本展示.
  
   
## 特性

+ Maven项目版本更新
    
    ![update-version](src/test/resources/picture/update-version.png)
    
    - 默认策略 
    
        - 必须存在新版本并且变更版本.
        - 当版本存在且匹配时将会替换；并且支持依赖版本是特殊值 (e.g: ${version} / [1.6, 1.8]) 将会跳过替换.
    
    - 常规策略 
    
        - 必须存在新版本.
        - 当版本存在并且匹配时将替换.
     
    - 支持必须同一版本 (变更前) 
     
        - 选中: 项目或依赖版本如果不等于替换之前的版本将跳过替换.
        - 未选中: 新版本会直接替换.

     

+ Maven项目版本显示
    
    ![show-version](src/test/resources/picture/show-version.png) 
    
    - project view
   
    ![show-version-project-view](src/test/resources/picture/show-version-project-view.png) 

    - structure view

    ![show-version-structure-view](src/test/resources/picture/show-version-structure-view.png) 
          

## 国际化支持
  
  ### 版本要求
  
     v1.0.1及以上

  ### 语言配置优先级
  
    项目(PROJECT) > 全局(GLOBAL) > 系统(SYSTEM)

  ### 语言优先级
  
    语言_国家/地区.properties > 语言.properties > en.properties

  ### 语言命名规则
  
    方式一: 语言_国家/地区.properties，e.g: zh_CN.properties
  
    方式二: 语言.properties，e.g: zh.properties

  
  ### 系统内置语言
  
   + zh         中文
   + zh_CN      简体中文（中国）
   + zh_TW      中文（台湾）
   + en         英文
      
  
  ### 如何指定当前使用的语言
  
    可通过系统默认语言，也可在conf.properties中进行指定要使用的语言
    
  - 配置文件
   
    + 全局配置: 用户主目录/mpvp/conf.properties
     
    + 项目配置: 用户maven项目工作目录/.idea/mpvp/conf.properties
   
  - 配置文件参考
   
     [src/test/resources/language-sample/mpvp/conf.properties](src/test/resources/language-sample/mpvp/conf.properties)
   
  
  ### 如何自定义语言及覆盖内置语言内容
    
  - 覆盖功能介绍
    
    当配置目录中存在当前使用语言的对应key的内容时，将优先进行使用用户语言资源，若不存在则使用内置语言资源进行兜底
    
  - 语言配置目录
  
    + 全局语言配置目录: 用户主目录/mpvp/language 
    
    + 项目语言配置目录: 用户maven项目工作目录/.idea/mpvp/language
  
  - 语言配置参考
  
     [src/test/resources/language-sample/mpvp/language](src/test/resources/language-sample/mpvp/language)

## 版本

    x.x.1    ->   idea 2017.3 - 2022.1
    x.x.2    ->   idea 2022.2 - 2023.2
    x.x.3    ->   idea 2023.3 - ?

## 安装

 - intellij-maven-project-version-plugin-1.0.1.zip -> [src/test/resources/distributions/intellij-maven-project-version-plugin-1.0.1.zip](src/test/resources/distributions/intellij-maven-project-version-plugin-1.0.1.zip)

 - intellij-maven-project-version-plugin-1.0.1.jar -> [src/test/resources/libs/intellij-maven-project-version-plugin-1.0.1.jar](src/test/resources/libs/intellij-maven-project-version-plugin-1.0.1.jar)



## 建议

  多模块中使用子模块依赖的version值推荐使用${project.version}来保持一致

## 其他

 - 插件菜单: Tools > Maven Project Version
 
 - 构建: build / intellij buildPlugin
 
 - 测试: intellij runIde
