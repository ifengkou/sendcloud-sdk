## sendcloud-sdk: 发短信、邮件、语音短信

由于sendcloud 提供的是 源码zip包[「源码路径」](https://www.sendcloud.net/doc/sdk/downloads/sendcloud-sdk.zip)，在工程集成时特别不方便

现将其发布成到Github上的私有maven仓库

### 使用

1. 在pom.xml 文件的 repositories 节点下添加 Github 私有maven仓库地址
```xml
    <repositories>
        <repository>
            <id>mvn-repo</id>
            <url>https://raw.github.com/ifengkou/mvn-repo/master</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
```
2. 在pom.xml 文件的 repositories 节点下添加项目的引用

```xml
    <dependencies>
        <dependency>
            <groupId>com.sendcloud</groupId>
            <artifactId>sendcloud-sdk</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>
```

3. 短信发送示例

```java
 public static void send() throws ClientProtocolException, IOException, SmsException {
        SendCloudSms sms = new SendCloudSms();
        sms.setMsgType(0);
        sms.setTemplateId(Integer.valueOf(948));
        sms.addPhone("12345678911,12345678910");
        sms.addVars("company", "爱发信");
        sms.addVars("date", "2016.04.02");
        SendCloud sc = SendCloudBuilder.build();
        ResponseData res = sc.sendSms(sms);
        System.out.println(res.getResult());
        System.out.println(res.getStatusCode());
        System.out.println(res.getMessage());
        System.out.println(res.getInfo());
    }
```

4. 发送语音

```java
public static void send() throws ParseException, IOException, VoiceException {
		SendCloudVoice voice = new SendCloudVoice();
		voice.setCode("123456");
		voice.setPhone("12345678910;12345678911");

		SendCloud sc = SendCloudBuilder.build();
		ResponseData res = sc.sendVoice(voice);

		System.out.println(res.getResult());
		System.out.println(res.getStatusCode());
		System.out.println(res.getMessage());
		System.out.println(res.getInfo());
	}
```

5. 发送邮件

```java
public static void send_common() throws Throwable {
		MailAddressReceiver receiver = new MailAddressReceiver();
		// 添加收件人
		receiver.addTo("a@ifaxin.com;b@ifaxin.com");
		// 添加抄送
		receiver.addCc("c@ifaxin.com");
		// 添加密送
		receiver.addBcc("d@ifaxin.com");

		MailBody body = new MailBody();
		// 设置 From
		body.setFrom("sendcloud@sendcloud.org");
		// 设置 FromName
		body.setFromName("SendCloud");
		// 设置 ReplyTo
		body.setReplyTo("reply@sendcloud.org");
		// 设置标题
		body.setSubject("来自 SendCloud SDK 的邮件");
		// 创建文件附件
		body.addAttachments(new File("D:/1.png"));
		body.addAttachments(new File("D:/2.png"));
		//// 创建流附件
		// body.addAttachments(new FileInputStream(new File("D:/ff.png")));

		TextContent content = new TextContent();
		content.setContent_type(ScContentType.html);
		content.setText("<html><p>helo world</p></html>");

		SendCloudMail mail = new SendCloudMail();
		mail.setTo(receiver);
		mail.setBody(body);
		mail.setContent(content);

		SendCloud sc = SendCloudBuilder.build();
		ResponseData res = sc.sendMail(mail);
		System.out.println(res.getResult());
		System.out.println(res.getStatusCode());
		System.out.println(res.getMessage());
		System.out.println(res.getInfo());
	}
    // 高级功能
	public static void send_common_advanced() throws Throwable {
		MailAddressReceiver receiver = new MailAddressReceiver();
		// 添加收件人
		receiver.addTo("a@ifaxin.com");
		// 添加抄送
		receiver.addCc("b@ifaxin.com");
		// 添加密送
		receiver.addBcc("c@ifaxin.com");

		MailBody body = new MailBody();
		// 设置 From
		body.setFrom("sendcloud@sendcloud.org");
		// 设置 FromName
		body.setFromName("SendCloud");
		// 设置 ReplyTo
		body.setReplyTo("reply@sendcloud.org");
		// 设置标题
		body.setSubject("来自 SendCloud SDK 的邮件");
		// 创建文件附件
		body.addAttachments(new File("D:/1.png"));

		// 配置 Xsmtpapi 扩展字段
		List<String> toList = new ArrayList<String>();
		toList.add("d@ifaxin.com");
		toList.add("e@ifaxin.com");
		List<String> moneyList = new ArrayList<String>();
		moneyList.add("1000");
		moneyList.add("2000");
		List<String> nameList = new ArrayList<String>();
		nameList.add("a");
		nameList.add("b");
		Map<String, List<String>> sub = new HashMap<String, List<String>>();
		sub.put("%name%", nameList);
		sub.put("%money%", moneyList);
		// 此时, receiver 中添加的 to, cc, bcc 均会失效
		body.addXsmtpapi("to", toList);
		body.addXsmtpapi("sub", sub);
		body.addHeader("SC-Custom-test_key1", "test1");
		body.addHeader("NO-SC-Custom-test_key1", "test2");

		TextContent content = new TextContent();
		content.setContent_type(ScContentType.html);
		content.setText("<html><p>亲爱的 %name%: </p> 您本月的支出为: %money% 元.</p></html>");

		SendCloudMail mail = new SendCloudMail();
		mail.setTo(receiver);
		mail.setBody(body);
		mail.setContent(content);

		SendCloud sc = SendCloudBuilder.build();
		ResponseData res = sc.sendMail(mail);
		System.out.println(res.getResult());
		System.out.println(res.getStatusCode());
		System.out.println(res.getMessage());
		System.out.println(res.getInfo());
	}
    // 使用模版
	public static void send_template() throws Throwable {
		MailBody body = new MailBody();
		// 设置 From
		body.setFrom("sendcloud@sendcloud.org");
		// 设置 FromName
		body.setFromName("SendCloud");
		// 设置 ReplyTo
		body.setReplyTo("reply@sendcloud.org");
		// 设置标题
		body.setSubject("来自 SendCloud SDK 的邮件");
		// 创建文件附件
		body.addAttachments(new File("D:/1.png"));

		List<String> toList = new ArrayList<String>();
		toList.add("a@ifaxin.com");
		toList.add("b@ifaxin.com");
		List<String> moneyList = new ArrayList<String>();
		moneyList.add("1000");
		moneyList.add("3000");
		List<String> nameList = new ArrayList<String>();
		nameList.add("a");
		nameList.add("b");
		Map<String, List<String>> sub = new HashMap<String, List<String>>();
		sub.put("%name%", nameList);
		sub.put("%code%", moneyList);
		// 此时, receiver 中添加的 to, cc, bcc 均会失效
		body.addXsmtpapi("to", toList);
		body.addXsmtpapi("sub", sub);
		body.addHeader("SC-Custom-test_key1", "test1");
		body.addHeader("NO-SC-Custom-test_key1", "test2");

		// 使用邮件模板
		TemplateContent content = new TemplateContent();
		content.setTemplateInvokeName("sendcloud_account_bind");

		SendCloudMail mail = new SendCloudMail();
		// 模板发送时, 必须使用 Xsmtpapi 来指明收件人; mail.setTo();
		mail.setBody(body);
		mail.setContent(content);

		SendCloud sc = SendCloudBuilder.build();
		ResponseData res = sc.sendMail(mail);
		System.out.println(res.getResult());
		System.out.println(res.getStatusCode());
		System.out.println(res.getMessage());
		System.out.println(res.getInfo());
	}
    // 批量发送
	public static void send_with_addresslist() throws Throwable {
		AddressListReceiver receiver = new AddressListReceiver();
		// 设置地址列表
		receiver.addTo("liubidatest@maillist.sendcloud.org");

		MailBody body = new MailBody();
		// 设置 From
		body.setFrom("sendcloud@sendcloud.org");
		// 设置 FromName
		body.setFromName("SendCloud");
		// 设置 ReplyTo
		body.setReplyTo("reply@sendcloud.org");
		// 设置标题
		body.setSubject("来自 SendCloud SDK 的邮件");
		// 创建文件附件
		body.addAttachments(new File("D:/1.png"));

		// 使用邮件模板
		TemplateContent content = new TemplateContent();
		content.setTemplateInvokeName("sendcloud_account_bind");

		SendCloudMail mail = new SendCloudMail();
		mail.setTo(receiver);
		mail.setBody(body);
		mail.setContent(content);

		SendCloud sc = SendCloudBuilder.build();
		ResponseData res = sc.sendMail(mail);
		System.out.println(res);
	}
```

6. 备注：sdk 使用文档：

```html
https://www.sendcloud.net/doc/sdk/
```