# pdfv
PDF Viewer


## mvn
### pdfv起動
```
mvn exec:java "-Dexec.mainClass=com.uchicom.pdfv.Main"
```

### フォーマッタ
```
mvn spotless:apply
```

### 全体テスト実行
```
mvn verify
```

#### ファイル単体でテスト実行
```
mvn test "-Dtest=com.uchicom.pdfv.MainTest"
```

### フォーマッタ & 全体テスト実行
```
mvn spotless:apply verify
```
