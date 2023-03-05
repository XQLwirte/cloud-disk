/**
 * 项目JS工具
 */
var util = {
	
	/**
	 * 封装Layui打开对话框
	 */
	open: function(param){
		//默认参数
		var defaultParam = {
			type: 1, 
			area: ["80%"],
			offset: 't',
			shadeClose: true //点击其他地方可以关闭
		};
		layui.jquery.extend(defaultParam,param);
		
		//获取页面内容
		var url = defaultParam.url;
		layui.jquery.get({
			url: url,
			data: defaultParam.data,
			success: function(content){
				defaultParam['content'] = content;
				//开启对话框
				layui.layer.open(defaultParam);
			}
		})
	},
	
	/**
	 * 封装Layui layer msg success
	 */
	success: function(msg){
		layer.msg(msg, {icon: 6});
	},
	
	/**
	 * 封装Layui layer msg wran
	 */
	warn: function(msg){
		layer.msg(msg, {icon: 0});
	},
	
	/**
	 * 封装Layui layer msg error
	 */
	error: function(msg){
		layer.msg(msg, {icon: 5});
	},
	
	
	/**
	 * 获取数据字典显示标签
	 */
	getValueLabel: function(dictValues,valueCode){
		for(dictValue of dictValues){
			if(dictValue.valueCode==valueCode)
				return dictValue.valueLabel;
		}
		return null;
	},
	
	/**
	 * data对象是否在数组中,在（true），不在（false）
	 */
	inArray: function(data, arr){
		for(item of arr){
			if(data==item)
				return true;
		}
		return false;
	},
	
	/**
	 * 文件下载
	 */
	download: function(url,name){
		window.location = url;
	},
	
	/**
	 * 检查是否为移动端屏幕
	 */
	checkMobile: function(){
		return document.body.clientWidth < 992;
	}

}