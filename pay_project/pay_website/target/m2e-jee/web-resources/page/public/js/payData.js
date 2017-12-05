$(function(){
	var payWayId='';
	//事件start
	$("body").on("click", ".choose", function() {
		$(".purseChoose img").attr("src","../public/img/c1.png");
		$(".choose").addClass("c1").removeClass("cs").parent().parent().attr("choose","");
		$(this).addClass("cs").removeClass("c1").parent().parent().attr("choose","choose");
	})
	$("body").on("click", ".purseChoose", function() {
		$(".purseChoose img").attr("src","../public/img/c.png");
		$(".choose").addClass("c1").removeClass("cs");
		payWayId=$(this).attr("payway");
	})
	$("body").on("click",".otherPurse",function(){
		$("body").css({"height":"100%","width":"100%","overflow":"hidden"})
		$(".bg").show();
		$(".otsPurse").show();
	})
	$("body").on("click",".bg",function(){
		$(".bg").hide();
		$(".otsPurse").hide();
	})
	$("body").on("click",".otsImg",function(){
		$("body").css({"height":"","width":"","overflow":""})
		$(".bg").hide();
		$(".otsPurse").hide();
	})
	$("body").on("click",".choose",function(){
		payWayId=$(this).attr("payway");
	})
	$("body").on("click",".ots",function(){
		$(".purseChoose").attr("payway",$(this).attr("payway"));
		$(".psPays").attr("urls",$(this).attr("urls"));
		$(".purseIcon img").attr("src",$(this).children().children("img").attr("src")) ;
		$(".purseName").html($(this).children().children(".bankUp").html());
		$(".purseOnsale").html($(this).children().children(".bankDown").html());
		$(".reduceMoney").html($(this).children(".otSale").html());
		$(this).children(".checked").css({"background":"url(../public/img/checked.png)","backgroundSize":"100% 100%"});
		$(this).siblings().children(".checked").css("background","url()");
		$(".bg").hide();
		$(".otsPurse").hide();
	})
	$("body").on("click",".downloadPurse",function(){
		window.location.href=$(".psPays").attr("urls");
	})
	$("body").on("click",".btn",function(){
		var param={};
		param.payWayId=payWayId;
		param.orderNum=orderNum;
		$.ajax({
			type:"get",
			url:"/payDetail/payH5Alipay.do",
			async:true,
			data:param,
			success:function(data){
				document.write(data);
			}
		});
	})
	//事件end
	//页面传参处理
	var obj=transmit();
	$.ajax({
		type: "get",
		url: "/payDetail/payOrderDetail.do",
		data: obj,
		async: true,
		success: function(data) {
			data=JSON.parse(data);
			console.log(data);
			if(!!data&&data.resultCode==200&&data.message!="支付已完成"&&data.message!="订单错误"){
				data=data.Data;
				payWayId=data.walletList[0].id;
				orderNum=data.orderNum;
				initializeData(data);
			}else if(data.message=="支付已完成"){
				$("body").html("")
				alert("支付已完成");
				window.history.back(-1);
			}
			else if(data.message=="订单错误"){
				$("body").html("")
				alert("订单错误");
				window.history.back(-1);
			}else{
				alert(data.message)
			}

		}
	
	});
})
function transmit(){
	console.log(window.location.search)
	var strings = window.location.search;
	var appkey = strings.indexOf("appKey=");
	var bussOrderNum = strings.indexOf("&bussOrderNum=");
	var payMoney = strings.indexOf("&payMoney=");
	var userDeviceToken = strings.indexOf("&userDeviceToken=");
	var orderName = strings.indexOf("&orderName=");
	var obj={};
	obj.appKey=strings.slice(7+appkey,bussOrderNum);
	obj.bussOrderNum=strings.slice(14+bussOrderNum,payMoney);
	obj.payMoney=Number(strings.slice(10+payMoney,userDeviceToken));
	obj.userDeviceToken=strings.slice(17+userDeviceToken,orderName);
	obj.orderName=strings.slice(11+orderName,strings.length);
	obj.orderName=decodeURI(obj.orderName);
	return obj
}

function initializeData(result) {
	//判断平台
	
	var urls = platform(result);
	$(".odInforWords").html(result.orderName);
	$(".odMoney").html(result.payMoney);
	//初始化钱包支付方式
	$(".psPays").attr("urls",urls[0]);
	$(".purseChoose").attr("payway",result.walletList[0].id)
	$(".maxSale").html("最高优惠" + result.maxDiscountMoney + "元");
	$(".purseIcon img").attr("src", result.walletList[0].wayIcon);
	$(".purseName").html(result.walletList[0].wayName);
	$(".purseOnsale").html(result.walletList[0].waySlogan);
	$(".reduceMoney").html("-￥" + result.walletList[0].discountMoney);
	//其它钱包支付方式
	var length = result.walletList.length;
	var purseStr = "";
	var str = '<div class="ots"><div class="bankIcon fl"><img src="../public/img/tf.png" /></div><div class="bankIntro fl"><div class="bankUp">天府钱包</div><div class="bankDown">每日限量单先到先得罄</div></div><div class="fr checked"></div><div class="fr otSale">-￥3.80</div></div>'
	for(var i = 0; i < length; i++) {
		purseStr += str;
	}
	$('.otsCon').html(purseStr);
	for(var i = 0; i < length; i++) {
		$(".otsCon .ots").eq(i).attr("urls", urls[i])
		$(".otsCon .ots").eq(i).attr("payway", result.walletList[i].id)
		$(".otsCon .ots").eq(i).children().find(".bankIcon img").attr("src", result.walletList[i].wayIcon);
		$(".otsCon .ots").eq(i).children().find(".bankUp").html(result.walletList[i].wayName);
		$(".otsCon .ots").eq(0).children(".checked").css({"background":"url(../public/img/checked.png)","backgroundSize":"100% 100%"});
		$(".otsCon .ots").eq(i).children().find(".bankDown").html(result.walletList[i].waySlogan);
		$(".otsCon .ots").eq(i).children(".otSale").html("-￥" + result.walletList[i].discountMoney);
	}
	//其他支付方式
	var otherStr = "";
	var length = result.otherList.length;
	var otherstr = '<div class="cardPay" choose=""><div class="cpCon"><div class="logoIcon fl"><img src="../public/img/bsb.png" /></div><div class="words fl"><div class="wsUp">银行卡支付</div><div class="wsDown">安全快速无需开通网银</div></div><div class="choose fr c1"></div></div></div>'
	for(var i = 0; i < length; i++) {
		otherStr += otherstr;
	}
	$('.othersPay').html(otherStr);
	for(var i = 0; i < length; i++) {
		$(".othersPay .cardPay").eq(i).children().find(".choose").attr("payway",result.otherList[i].id)
		$(".othersPay .cardPay").eq(i).children().find(".logoIcon img").attr("src", result.otherList[i].wayIcon);
		$(".othersPay .cardPay").eq(i).children().children().find(".wsUp").html(result.otherList[i].wayName);
		$(".othersPay .cardPay").eq(i).children().children().find(".wsDown").html(result.otherList[i].waySlogan);
		$(".othersPay .cardPay").eq(i).children(".otSale").html("-￥" + result.otherList[i].discountMoney);
	}
}

//判断用什么客户端打开
function platform(result) {
	var urls=[];
	var browser = {
		versions: function() {
			var u = navigator.userAgent,
				app = navigator.appVersion;
			return { //移动终端浏览器版本信息
				trident: u.indexOf('Trident') > -1, //IE内核
				presto: u.indexOf('Presto') > -1, //opera内核
				webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
				gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
				mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
				ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
				android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或uc浏览器
				iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
				iPad: u.indexOf('iPad') > -1, //是否iPad
				webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
			};
		}(),
		language: (navigator.browserLanguage || navigator.language).toLowerCase()
	}
	if(browser.versions.mobile) {
		if(browser.versions.ios) {
			var length = result.walletList.length;
			for(var i = 0; i < length; i++) {
				urls.push(result.walletList[i].wayIosUrl);
			}
		}
		if(browser.versions.android) {
			var length = result.walletList.length;
			for(var i = 0; i < length; i++) {
				urls.push(result.walletList[i].wayApkUrl)
			}
		}
	}
	return urls;
}