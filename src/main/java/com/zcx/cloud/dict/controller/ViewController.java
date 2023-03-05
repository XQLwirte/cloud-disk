package com.zcx.cloud.dict.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.dict.entity.DictKey;
import com.zcx.cloud.dict.entity.DictValue;
import com.zcx.cloud.dict.service.IDictKeyService;
import com.zcx.cloud.dict.service.IDictValueService;
import com.zcx.cloud.util.ViewUtil;

@Component("dictViewController")
@RequestMapping("/base/dict")
public class ViewController {
	@Autowired
	private IDictKeyService dictKeyService;
	@Autowired
	private IDictValueService dictValueService;

	@GetMapping("dictKey")
	public String dictKey() {
		return ViewUtil.view("base/dict/dictKey");
	}
	
	
	@GetMapping("dictKeyDetail/{keyId}")
	public String dictKeyDetail(@PathVariable("keyId")Long keyId, Model model) {
		DictKey dictKey = dictKeyService.getById(keyId);
		model.addAttribute(dictKey);
		return ViewUtil.view("base/dict/dictKeyDetail");
	}
	
	@GetMapping("dictKeyAdd")
	public String dictKeyAdd() {
		return ViewUtil.view("base/dict/dictKeyAdd");
	}
	
	@GetMapping("dictKeyEdit/{keyId}")
	public String dictKeyEdit(@PathVariable("keyId")Long keyId, Model model) {
		DictKey dictKey = dictKeyService.getById(keyId);
		model.addAttribute(dictKey);
		return ViewUtil.view("base/dict/dictKeyEdit");
	}
	
	
	
	@GetMapping("dictValue/{keyCode}")
	public String dictValue(@PathVariable("keyCode")String keyCode, Model model) {
		model.addAttribute(keyCode);
		return ViewUtil.view("base/dict/dictValue");
	}
	
	@GetMapping("dictValueDetail/{valueId}")
	public String dictValueDetail(@PathVariable("valueId")Long valueId, Model model) {
		DictValue dictValue = dictValueService.getById(valueId);
		model.addAttribute(dictValue);
		return ViewUtil.view("base/dict/dictValueDetail");
	}
	
	@GetMapping("dictValueAdd/{keyCode}")
	public String dictValueAdd(@PathVariable("keyCode")String keyCode, Model model) {
		model.addAttribute(keyCode);
		return ViewUtil.view("base/dict/dictValueAdd");
	}
	
	@GetMapping("dictValueEdit/{valueId}")
	public String dictValueEdit(@PathVariable("valueId")Long valueId, Model model) {
		DictValue dictValue = dictValueService.getById(valueId);
		model.addAttribute(dictValue);
		return ViewUtil.view("base/dict/dictValueEdit");
	}
}
