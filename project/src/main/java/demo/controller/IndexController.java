package demo.controller;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
public class IndexController {
    //填写WeBASE-Front地址，用于后续交互
    private static final String URL = "http://192.168.150.128:5002/WeBASE-Front/trans/handle";

    private static final String CONTRACT_NAME = "Trace";
    private static final String CONTRACT_ADDRESS = "0xa76a7636fbd955670372cf40e469c680aeacadc0";
    private static final String CONTRACT_ABI = "[{\"constant\":true,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"}],\"name\":\"getFood\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"}],\"name\":\"getTraceInfo\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"address[]\"},{\"name\":\"\",\"type\":\"uint8[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"traceNumber\",\"type\":\"uint256\"},{\"name\":\"traceName\",\"type\":\"string\"},{\"name\":\"quality\",\"type\":\"uint8\"}],\"name\":\"newFood\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"},{\"name\":\"traceName\",\"type\":\"string\"},{\"name\":\"quality\",\"type\":\"uint8\"}],\"name\":\"addTraceInfoByDistributor\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isRetailer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceDistributor\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getAllFood\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addDistributor\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addRetailer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isDistributor\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"traceNumber\",\"type\":\"uint256\"},{\"name\":\"traceName\",\"type\":\"string\"},{\"name\":\"quality\",\"type\":\"uint8\"}],\"name\":\"addTraceInfoByRetailer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceRetailer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addProducer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isProducer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceProducer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"producer\",\"type\":\"address\"},{\"name\":\"distributor\",\"type\":\"address\"},{\"name\":\"retailer\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"RetailerAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"RetailerRemoved\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"DistributorAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"DistributorRemoved\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"ProducerAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"ProducerRemoved\",\"type\":\"event\"}]";



    private static final String PRODUCER_ADDRESS = "0x230e0f8413bbf1b593379c55c2e318c72a01844d";
    private static final String DISTRIBUTOR_ADDRESS = "0x95ecd0a068193bb0b8a1044fc6180b126cd971a6";
    private static final String RETAILER_ADDRESS = "0xd05f0d6c75d414c47b1d289ef0e33c619a8d89b3";


    /*




    //@GetMapping("/index")把里面的index删了就能127.0.0.1：8080访问了，如果不删就是127.0.0.1：8080/index访问
    */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 获取用户地址
     * userinfo: 用户角色（producer=农场 distributor=生产/加工商 retailer=零售商）
     *
     * @return: 角色对应用户地址
     */
    @ResponseBody
    @GetMapping(path = "/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public String userInfo(String userName) {
        //声明返回对象
        JSONObject _outPut = new JSONObject();

        //返回各个用户的地址
        if (userName.equals("producer")) {
            _outPut.put("address", PRODUCER_ADDRESS);
        } else if (userName.equals("distributor")) {
            _outPut.set("address", DISTRIBUTOR_ADDRESS);
        } else if (userName.equals("retailer")) {
            _outPut.set("address", RETAILER_ADDRESS);
        } else {
            _outPut.set("error", "user not found");
        }
        return JSONUtil.toJsonStr(_outPut);
    }

    /**
     * 添加食品生产信息
     * traceNumber: 食品溯源id，食品溯源过程中的标识符（必须为正整数）
     * foodName: 食物名称
     * traceName: 用户名，食品流转过程各个阶段的用户名
     * quality: 当前食品质量（0=优质 1=合格 2=不合格）
     *
     * @return：添加食品生产信息结果
     */
    @ResponseBody
    @PostMapping(path = "/produce", produces = MediaType.APPLICATION_JSON_VALUE)
    public String produce(@RequestBody JSONObject jsonParam) {
        JSONObject _outPutObj = new JSONObject();

        if (jsonParam == null) {
            _outPutObj.set("code", 400);
            _outPutObj.set("msg", "参数不能为空");
            return JSONUtil.toJsonStr(_outPutObj);
        }

        try {
            // ✅ 1. 检查 traceNumber 是否存在
            if (!jsonParam.containsKey("traceNumber")) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 2. 获取 traceNumber 并校验类型
            Object traceObj = jsonParam.get("traceNumber");
            long trace_number;

            // 判断类型
            if (traceObj instanceof Integer) {
                // 是 int 类型
                trace_number = ((Integer) traceObj).longValue();
            } else if (traceObj instanceof Long) {
                // 是 long 类型
                trace_number = (Long) traceObj;
            } else if (traceObj instanceof String) {
                // 是字符串，尝试转为数字
                try {
                    trace_number = Long.parseLong((String) traceObj);
                } catch (NumberFormatException e) {
                    _outPutObj.set("code", 400);
                    _outPutObj.set("msg", "traceNumber 必须是数字，不能包含字母或特殊字符");
                    return JSONUtil.toJsonStr(_outPutObj);
                }
            } else {
                // 其他类型（boolean, double, array, object 等）
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 必须是整数类型，当前类型为: " + traceObj.getClass().getSimpleName());
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 3. 校验必须是正整数
            if (trace_number <= 0) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 必须是正整数（大于0）");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 4. 校验 traceNumber 不能超过 int 范围（如果需要）
            if (trace_number > Integer.MAX_VALUE) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 不能超过 " + Integer.MAX_VALUE);
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 5. 获取其他参数
            String food_name = jsonParam.getStr("foodName");
            if (food_name == null || food_name.trim().isEmpty()) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "foodName 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            String trace_name = jsonParam.getStr("traceName");
            if (trace_name == null || trace_name.trim().isEmpty()) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceName 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            Object qualityObj = jsonParam.get("quality");
            int quality;
            if (qualityObj instanceof Integer) {
                quality = (Integer) qualityObj;
            } else if (qualityObj instanceof String) {
                try {
                    quality = Integer.parseInt((String) qualityObj);
                } catch (NumberFormatException e) {
                    _outPutObj.set("code", 400);
                    _outPutObj.set("msg", "quality 必须是数字（0=优质 1=合格 2=不合格）");
                    return JSONUtil.toJsonStr(_outPutObj);
                }
            } else {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "quality 必须是整数类型");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            if (quality < 0 || quality > 2) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "quality 必须是 0（优质）、1（合格）或 2（不合格）");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 6. 构造参数调用合约
            JSONArray params = new JSONArray();
            params.add(food_name);
            params.add((int) trace_number);  // 转为 int
            params.add(trace_name);
            params.add(quality);

            String responseStr = httpPost(PRODUCER_ADDRESS, "newFood", params);
            JSONObject responseJsonObj = JSONUtil.parseObj(responseStr);
            String msg = responseJsonObj.getStr("message");

            if ("Success".equals(msg)) {
                _outPutObj.set("code", 200);
                _outPutObj.set("ret", 1);
                _outPutObj.set("msg", "食品添加成功");
                _outPutObj.set("data", responseJsonObj);
            } else {
                _outPutObj.set("code", 500);
                _outPutObj.set("ret", 0);
                _outPutObj.set("msg", msg != null ? msg : "合约调用失败");
            }

        } catch (Exception e) {
            _outPutObj.set("code", 500);
            _outPutObj.set("ret", 0);
            _outPutObj.set("msg", "系统错误: " + e.getMessage());
            e.printStackTrace();
        }

        return JSONUtil.toJsonStr(_outPutObj);
    }

    /**
     * 生产/加工商添加食品流转信息
     * traceNumber: 食品溯源id，食品溯源过程中的标识符
     * traceName: 用户名，食品流转过程各个阶段的用户名
     * quality: 当前食品质量（0=优质 1=合格 2=不合格）
     *
     * @return：生产/加工商添加食品流转信息结果
     */
    @ResponseBody
    @PostMapping(path = "/adddistribution", produces = MediaType.APPLICATION_JSON_VALUE)
    public String add_trace_by_distrubutor(@RequestBody JSONObject jsonParam) {

        JSONObject _outPutObj = new JSONObject();

        if (jsonParam == null) {
            _outPutObj.set("code", 400);
            _outPutObj.set("msg", "参数不能为空");
            return JSONUtil.toJsonStr(_outPutObj);
        }

        try {
            // ✅ 1. 校验 traceNumber
            if (!jsonParam.containsKey("traceNumber")) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }
            int trace_number = jsonParam.getInt("traceNumber");
            if (trace_number <= 0) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 必须是正整数");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 2. 校验 traceName（用 getStr 避免空指针）
            String trace_name = jsonParam.getStr("traceName");
            if (trace_name == null || trace_name.trim().isEmpty()) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceName 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 3. 校验 quality
            if (!jsonParam.containsKey("quality")) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "quality 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }
            int quality = jsonParam.getInt("quality");
            if (quality < 0 || quality > 2) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "quality 必须是 0、1 或 2");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 4. 构造参数调用合约
            JSONArray params = new JSONArray();
            params.add(trace_number);
            params.add(trace_name);
            params.add(quality);

            String responseStr = httpPost(DISTRIBUTOR_ADDRESS, "addTraceInfoByDistributor", params);
            System.out.println("WeBASE返回原始数据: " + responseStr);
            JSONObject responseJsonObj = JSONUtil.parseObj(responseStr);
            String msg = responseJsonObj.getStr("message");

            if ("Success".equals(msg)) {
                _outPutObj.set("code", 200);
                _outPutObj.set("ret", 1);
                _outPutObj.set("msg", "分销信息添加成功");
            } else {
                _outPutObj.set("code", 500);
                _outPutObj.set("ret", 0);
                _outPutObj.set("msg", msg != null ? msg : "合约调用失败");
            }

        } catch (Exception e) {
            _outPutObj.set("code", 500);
            _outPutObj.set("ret", 0);
            _outPutObj.set("msg", "系统错误: " + e.getMessage());
            e.printStackTrace();
        }

        return JSONUtil.toJsonStr(_outPutObj);
    }

    /**
     * 零售商添加食品流转信息
     * traceNumber: 食品溯源id，食品溯源过程中的标识符
     * traceName: 用户名，食品流转过程各个阶段的用户名
     * quality: 当前食品质量（0=优质 1=合格 2=不合格）
     *
     * @param jsonParam
     * @return 零售商添加食品流转信息结果
     */
    @ResponseBody
    @PostMapping(path = "/addretail", produces = MediaType.APPLICATION_JSON_VALUE)
    public String add_trace_by_retailer(@RequestBody JSONObject jsonParam) {

        JSONObject _outPutObj = new JSONObject();

        if (jsonParam == null) {
            _outPutObj.set("code", 400);
            _outPutObj.set("msg", "参数不能为空");
            return JSONUtil.toJsonStr(_outPutObj);
        }

        try {
            // ✅ 1. 校验 traceNumber
            if (!jsonParam.containsKey("traceNumber")) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }
            int trace_number = jsonParam.getInt("traceNumber");
            if (trace_number <= 0) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceNumber 必须是正整数");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 2. 校验 traceName（用 getStr 避免空指针）
            String trace_name = jsonParam.getStr("traceName");
            if (trace_name == null || trace_name.trim().isEmpty()) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "traceName 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 3. 校验 quality
            if (!jsonParam.containsKey("quality")) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "quality 是必填字段");
                return JSONUtil.toJsonStr(_outPutObj);
            }
            int quality = jsonParam.getInt("quality");
            if (quality < 0 || quality > 2) {
                _outPutObj.set("code", 400);
                _outPutObj.set("msg", "quality 必须是 0、1 或 2");
                return JSONUtil.toJsonStr(_outPutObj);
            }

            // ✅ 4. 构造参数调用合约
            JSONArray params = new JSONArray();
            params.add(trace_number);
            params.add(trace_name);
            params.add(quality);

            String responseStr = httpPost(RETAILER_ADDRESS, "addTraceInfoByRetailer", params);
            JSONObject responseJsonObj = JSONUtil.parseObj(responseStr);
            String msg = responseJsonObj.getStr("message");

            if ("Success".equals(msg)) {
                _outPutObj.set("code", 200);
                _outPutObj.set("ret", 1);
                _outPutObj.set("msg", "零售信息添加成功");
            } else {
                _outPutObj.set("code", 500);
                _outPutObj.set("ret", 0);
                _outPutObj.set("msg", msg != null ? msg : "合约调用失败");
            }

        } catch (Exception e) {
            _outPutObj.set("code", 500);
            _outPutObj.set("ret", 0);
            _outPutObj.set("msg", "系统错误: " + e.getMessage());
            e.printStackTrace();
        }

        return JSONUtil.toJsonStr(_outPutObj);
    }


    /**
     * # 获取所有食物信息
     *
     * @return 所有食品信息列表
     */
    @ResponseBody
    @GetMapping(path = "/foodlist", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getlist() {
        JSONArray num_list = get_food_list();
        JSONArray num_list2 = num_list.getJSONArray(0);
        JSONArray resList = new JSONArray();

        for (int i = 0; i < num_list2.size(); i++) {
            String food = get_food(num_list2.get(i).toString());
            resList.add(food);
        }
        return JSONUtil.toJsonStr(resList);
    }


    /**
     * 获取某个食品的溯源信息
     *
     * @param traceNumber 食品溯源id，食品溯源过程中的标识符
     * @return 对应食品的溯源信息
     */
    @ResponseBody
    @GetMapping(path = "/trace", produces = MediaType.APPLICATION_JSON_VALUE)
    public String trace(String traceNumber) {

        JSONObject _outPut = new JSONObject();

        if (Integer.parseInt(traceNumber) <= 0) {
            _outPut.put("error", "invalid parameter");
            return JSONUtil.toJsonStr(_outPut);
        }

        List res = get_trace(traceNumber);
        JSONArray o = new JSONArray(res);
        return JSONUtil.toJsonStr(o);

    }

    /**
     * 获取某个食品的当前信息
     *
     * @param traceNumber 食品溯源id，食品溯源过程中的标识符
     * @return 对应食品的当前信息
     */
    @ResponseBody
    @GetMapping(path = "/food", produces = MediaType.APPLICATION_JSON_VALUE)
    public String food(String traceNumber) {

        JSONObject _outPut = new JSONObject();

        if (Integer.parseInt(traceNumber) <= 0) {
            _outPut.set("error", "invalid parameter");
            return JSONUtil.toJsonStr(_outPut);
        }

        String res = get_food(traceNumber);
        return res;

    }


    /**
     * 获取所有食品的最新溯源信息
     *
     * @return 所有食品的最新溯源信息
     */
    @ResponseBody
    @GetMapping(path = "/newtracelist", produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_latest() {
        JSONArray num_list = get_food_list();
        JSONArray num_list2 = num_list.getJSONArray(0);
        JSONArray resList = new JSONArray();

        for (int i = 0; i < num_list2.size(); i++) {
            List trace = get_trace(num_list2.get(i).toString());
            resList.add(trace.get(-1));
        }
        return JSONUtil.toJsonStr(resList);
    }


    /**
     * 获取位于供应商的的食物信息
     *
     * @return 所有位于供应商的食品信息列表
     */
    @ResponseBody
    @GetMapping(path = "/producing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_producing() {
        JSONArray num_list = get_food_list();
        JSONArray num_list2 = num_list.getJSONArray(0);
        JSONArray resList = new JSONArray();

        for (int i = 0; i < num_list2.size(); i++) {
            JSONArray trace = get_trace(num_list2.get(i).toString());
            if (trace.size() == 1) {
                resList.add(trace.get(0));
            }
        }
        String a = JSONUtil.toJsonStr(resList);
        return JSONUtil.toJsonStr(resList);
    }

    /**
     * 获取位于生产/加工商的食物信息
     *
     * @return 所有位于生产/加工商的食品信息列表
     */
    @ResponseBody
    @GetMapping(path = "/distributing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_distributing() {
        JSONArray num_list = get_food_list();
        JSONArray num_list2 = num_list.getJSONArray(0);
        JSONArray resList = new JSONArray();

        for (int i = 0; i < num_list2.size(); i++) {
            List trace = get_trace(num_list2.get(i).toString());
            if (trace.size() == 2) {
                resList.add(trace.get(1));
            }
        }
        return JSONUtil.toJsonStr(resList);
    }


    /**
     * 获取位于零售商的食物信息
     *
     * @return 所有位于零售商的食品信息列表
     */
    @ResponseBody
    @GetMapping(path = "/retailing", produces = MediaType.APPLICATION_JSON_VALUE)
    public String get_retailing() {

        JSONArray num_list = get_food_list();
        List num_list2 = num_list.getJSONArray(0);
        JSONArray resList = new JSONArray();
        for (int i = 0; i < num_list2.size(); i++) {
            List trace = get_trace(num_list2.get(i).toString());
            if (trace.size() == 3) {
                resList.add(trace.get(2));
            }
        }
        return JSONUtil.toJsonStr(resList);
    }

    /**
     * # 从链上获取所有食品信息
     *
     * @return 所有食品信息列表
     */
    private JSONArray get_food_list() {

        String responseStr = httpPost(PRODUCER_ADDRESS, "getAllFood", new ArrayList());
        JSONArray responseJsonObj = JSONUtil.parseArray(responseStr);
        return responseJsonObj;
    }


    /**
     * 从链上获取某个食品的基本信息
     *
     * @param traceNumber: 食品溯源id，食品溯源过程中的标识符
     * @return 对应食品的信息
     */
    private String get_food(String traceNumber) {
        JSONArray params = JSONUtil.parseArray("[" + traceNumber + "]");

        String responseStr = httpPost(PRODUCER_ADDRESS, "getFood", params);
        JSONArray food = JSONUtil.parseArray(responseStr);

        // ✅ 获取溯源信息判断状态
        String traceStr = httpPost(PRODUCER_ADDRESS, "getTraceInfo", params);
        JSONArray traceInfo = JSONUtil.parseArray(traceStr);
        int recordCount = 0;
        if (traceInfo.size() > 0) {
            JSONArray timeList = traceInfo.getJSONArray(0);
            if (timeList != null) {
                recordCount = timeList.size();
            }
        }

        JSONObject _outPut = new JSONObject();
        _outPut.set("traceNumber", traceNumber);
        _outPut.set("timestamp", food.get(0));
        _outPut.set("produce", food.get(1));
        _outPut.set("name", food.get(2));
        _outPut.set("current", food.get(3));
        _outPut.set("address", food.get(4));
        _outPut.set("quality", food.get(5));

        // ✅ 根据记录数量判断状态
        int status;
        if (recordCount == 1) {
            status = 0;  // 生产中
        } else if (recordCount == 2) {
            status = 1;  // 分销中
        } else {
            status = 2;  // 已出售
        }
        _outPut.set("status", status);

        return JSONUtil.toJsonStr(_outPut);
    }

    /**
     * 从链上获取某个食品的溯源信息
     *
     * @param traceNumber 食品溯源id，食品溯源过程中的标识符
     * @return 对应食品的溯源信息
     */
    private JSONArray get_trace(String traceNumber) {
        //获取食品基本信息
        JSONArray params = JSONUtil.parseArray("[" + traceNumber + "]");

        String responseStr = httpPost(PRODUCER_ADDRESS, "getFood", params);
        JSONArray food = JSONUtil.parseArray(responseStr);

        //获取食品溯源信息
        String responseStr2 = httpPost(PRODUCER_ADDRESS, "getTraceInfo", params);
        JSONArray traceInfoList = JSONUtil.parseArray(responseStr2);
        JSONArray time_list = traceInfoList.getJSONArray(0);
        JSONArray name_list = traceInfoList.getJSONArray(1);
        JSONArray address_list = traceInfoList.getJSONArray(2);
        JSONArray quality_list = traceInfoList.getJSONArray(3);

        JSONArray _outPut = new JSONArray();
        for (int i = 0; i < time_list.size(); i++) {
            if (i == 0) {
                JSONObject _outPutObj = new JSONObject();
                _outPutObj.set("traceNumber", traceNumber);
                _outPutObj.set("name", food.get(2));
                _outPutObj.set("produce_time", food.get(0));
                _outPutObj.set("timestamp", time_list.get(i));
                _outPutObj.set("from", name_list.get(i));
                _outPutObj.set("quality", quality_list.get(i));
                _outPutObj.set("from_address", address_list.get(i));
                _outPut.add(_outPutObj);
            } else {
                JSONObject _outPutObj = new JSONObject();
                _outPutObj.set("traceNumber", traceNumber);
                _outPutObj.set("name", food.get(2));
                _outPutObj.set("produce_time", food.get(0));
                _outPutObj.set("timestamp", time_list.get(i));
                _outPutObj.set("from", name_list.get(i - 1));
                _outPutObj.set("to", name_list.get(i));
                _outPutObj.set("quality", quality_list.get(i));
                _outPutObj.set("from_address", address_list.get(i - 1));
                _outPutObj.set("to_address", address_list.get(i));
                _outPut.add(_outPutObj);
            }
        }
        return _outPut;
    }


    /**
     * 基本http函数
     *
     * @param address
     * @param funcName
     * @param funcParam
     * @return java.lang.String
     * @author huangyu@ivinsight.com
     * @date 2022/4/28
     */

    private String httpPost(String address, String funcName, List funcParam) {
        JSONObject _jsonObj = new JSONObject();
        _jsonObj.set("contractName", CONTRACT_NAME);
        _jsonObj.set("contractAddress", CONTRACT_ADDRESS);
        _jsonObj.set("contractAbi", JSONUtil.parseArray(CONTRACT_ABI));
        _jsonObj.set("user", address);
        _jsonObj.set("funcName", funcName);
        _jsonObj.set("funcParam", funcParam);
        _jsonObj.set("groupId", 1);
        _jsonObj.set("useCns", false);

        String dataString = JSONUtil.toJsonStr(_jsonObj);
        String responseBody = HttpRequest.post(URL)
                .header(Header.CONTENT_TYPE, "application/json").body(dataString).execute().body();
        return responseBody;

    }
}