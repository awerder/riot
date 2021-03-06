<#import "/spring.ftl" as spring />

<div id="status" class="box">
	<div class="box-title">
		<span class="label"><@spring.messageText "label.status.title", "Status" /></span>
	</div>
	<div id="statusMessages">
		<#if messages?has_content>
			<#list messages as message>
				<div class="message">
					<#if message.link?exists>
						<a href="${riot.href(message.link)}"><div class="icon"></div><div class="text">${message.text}</div></a>
					<#else>
						<div class="icon"></div><div class="text">${message.text}</div>
					</#if>
				</div>
			</#list>
			<div style="clear: both;"></div>
		</#if>
	</div>
</div>