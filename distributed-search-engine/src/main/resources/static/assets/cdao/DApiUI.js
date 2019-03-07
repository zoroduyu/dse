/**
 * Created by xiaoym on 2017/4/17.
 */

(function ($) {
    //初始化类
    var DApiUI={};
    var apiData;

    DApiUI.init=function () {
        $.ajax({
            url:"v2/api-docs",
            //url:"menu.json",
            dataType:"json",
            type:"get",
            async:false,
            success:function (data) {
                //var menu=JSON.parse(data)
                var menu=data;
                DApiUI.definitions(menu);
                DApiUI.log(menu);
                DApiUI.createDescription(menu);
                DApiUI.initTreeMenu(menu);
                DApiUI.eachPath(menu);
                apiData = menu;
            }
        })
    }

    /***
     * 创建面板
     */
    DApiUI.creatabTab=function () {
        var debug = apiData.info.license.name;
        var divcontent=$('<div id="myTab" class="tabs-container" style="width:95%;margin:0px auto;"></div>');

        var ul=$('<ul class="nav nav-tabs"></ul>')
        var liapi=$('<li><a data-toggle="tab" href="#tab1" aria-expanded="false"> 接口说明</a></li>');
        ul.append(liapi);
        if(typeof (debug) != 'undefined' && debug != null && debug == "debug"){
            var lidebug=$('<li class=""><a data-toggle="tab" href="#tab2" aria-expanded="true"> 在线调试</a></li>');
            ul.append(lidebug);
        }
        var liResponse=$('<li><a data-toggle="tab" href="#tab3" aria-expanded="false"> 响应参数</a></li>');
        ul.append(liResponse);
        divcontent.append(ul);

        var tabcontent=$('<div class="tab-content"></div>');
        var tab1content=$('<div id="tab1" class="tab-pane"><div class="panel-body"><strong>接口详细说明</strong><p>Bootstrap 使用到的某些 HTML 元素和 CSS 属性需要将页面设置为 HTML5 文档类型。在你项目中的每个页面都要参照下面的格式进行设置。</p></div></div>');
        tabcontent.append(tab1content);
        var tab2content=$('<div id="tab2" class="tab-pane"><div class="panel-body"><strong>正在开发中,敬请期待......</strong></div></div>');
        tabcontent.append(tab2content);
        var tab3content=$('<div id="tab3" class="tab-pane"><div class="panel-body"><strong>响应参数</strong></div></div>');
        tabcontent.append(tab3content);
        divcontent.append(tabcontent);

        //内容覆盖
        DApiUI.getDoc().html("");
        DApiUI.getDoc().append(divcontent);
        DApiUI.log("动态激活...")
        //liapi.addClass("active");
        DApiUI.log("动态激活12...")
        DApiUI.getDoc().find("#myTab a:first").tab('show')
        //$('#myTab a:first').tab('show')

    }


    /***
     * 创建简介table页面
     * @param menu
     */
    DApiUI.createDescription=function (menu) {
        var table=$('<table class="table table-hover table-bordered table-text-center"></table>');
        var thead=$('<thead><tr><th colspan="2" style="text-align:center">' + menu.info.title + '-前后端api接口文档</th></tr></thead>');
        table.append(thead);
        var tbody=$('<tbody></tbody>');
        var title=$('<tr><th class="active">项目名称</th><td style="text-align: left">'+menu.info.title+'</td></tr>');
        tbody.append(title);
        var description=$('<tr><th class="active">简介</th><td style="text-align: left">'+menu.info.description+'</td></tr>');
        tbody.append(description);
        var author=$('<tr><th class="active">作者</th><td style="text-align: left">'+menu.info.contact.name+'</td></tr>')
        tbody.append(author);
        var version=$('<tr><th class="active">版本</th><td style="text-align: left">'+menu.info.version+'</td></tr>')
        tbody.append(version);
        var host=$('<tr><th class="active">host</th><td style="text-align: left">'+menu.host+'</td></tr>')
        tbody.append(host)
        var service=$('<tr><th class="active">服务url</th><td style="text-align: left">'+menu.info.termsOfService+'</td></tr>')
        tbody.append(service);
        table.append(tbody);

        var div=$('<div  style="width:95%;margin:0px auto;"></div>')
        div.append(table);
        //内容覆盖
        DApiUI.getDoc().html("");
        DApiUI.getDoc().append(div);
        DApiUI.getDoc().data("data",menu);
    }

    /***
     * 初始化菜单树
     * @param menu
     */
    DApiUI.initTreeMenu=function (menu) {
        //遍历tags
        var tags=new Array();
        //简介li
        var dli=$('<li  class="active"><a href="javascript:void(0)"><i class="icon-text-width"></i><span class="menu-text"> 简介 </span></a></li>')
        dli.on("click",function () {
            DApiUI.log("简介click")
            DApiUI.createDescription(menu);
            dli.addClass("active");
        })
        DApiUI.getMenu().html("");
        DApiUI.getMenu().append(dli);
        var methodApis=DApiUI.eachPath(menu);

        $.each(menu.tags,function (i, tag) {
            var tagInfo=new TagInfo(tag.name,tag.description);
            //查找childrens
            $.each(methodApis,function (i, methodApi) {
                //判断tags是否相同
                if(methodApi.tag==tagInfo.name){
                    tagInfo.childrens.push(methodApi);
                }
            })
            var len=tagInfo.childrens.length;
            if(len==0){
                var li=$('<li ><a href="javascript:void(0)"><i class="icon-text-width"></i><span class="menu-text"> '+tagInfo.name+' </span></a></li>');
                DApiUI.getMenu().append(li);
            }else{
                //存在子标签
                var li=$('<li></li>');
                var titleA=$('<a href="#" class="dropdown-toggle"><i class="icon-file-alt"></i><span class="menu-text">'+tagInfo.name+'<span class="badge badge-primary ">'+len+'</span></span></a>');
                li.append(titleA);
                //循环树
                var ul=$('<ul class="submenu"></ul>')
                $.each(tagInfo.childrens,function (i, children) {
                    var childrenLi=$('<li class="menuLi"></li>');
                    var childrenA=$('<a href="javascript:void(0)"><i class="icon-double-angle-right"></i>'+children.summary+'</a>');
                    childrenLi.append(childrenA);
                    childrenLi.data("data",children);
                    ul.append(childrenLi);
                })
                li.append(ul);
                DApiUI.getMenu().append(li);
            }
        })
        DApiUI.log("菜单初始化完成...")
        DApiUI.initLiClick();
    }

    DApiUI.container=function (array, prop, value) {
        var temp;
        for(var i = 0; i < array.length; i++){
            var obj = array[i];
            if(obj.hasOwnProperty(prop) && obj[prop] == value){
                temp = obj;
                break;
            }
        }
        return temp;
    }

    DApiUI.eachPath=function (menu) {
        var paths=menu.paths;
        DApiUI.log(paths);
        //paths是object对象,key是api接口地址,
        var methodApis=[];
        for(var key in paths){
            var obj=paths[key];
            //遍历obj,获取api接口访问方式
            //八中方式类型,直接判断
            var apiInfo = null;
            if(obj.hasOwnProperty("get")){
                //get方式
                apiInfo=new ApiInfo(obj["get"]);
                apiInfo.methodType="get";
                apiInfo.methodTypes = apiInfo.methodType;
                apiInfo.url=key;
            }

            if(obj.hasOwnProperty("post")){
                if(typeof (apiInfo) == 'undefined' || apiInfo == null){
                    apiInfo=new ApiInfo(obj["post"]);
                    apiInfo.methodType="post";
                    apiInfo.methodTypes = apiInfo.methodType;
                    apiInfo.url=key;
                } else {
                    apiInfo.methodTypes = apiInfo.methodTypes + "，post";
                }
            }

            if(obj.hasOwnProperty("put")){
                if(typeof (apiInfo) == 'undefined' || apiInfo == null){
                    apiInfo=new ApiInfo(obj["put"]);
                    apiInfo.methodType="put";
                    apiInfo.methodTypes = apiInfo.methodType;
                    apiInfo.url=key;
                } else {
                    apiInfo.methodTypes = apiInfo.methodTypes + "，put";
                }
            }

            if(obj.hasOwnProperty("delete")){
                if(typeof (apiInfo) == 'undefined' || apiInfo == null){
                    apiInfo=new ApiInfo(obj["delete"]);
                    apiInfo.methodType="delete";
                    apiInfo.methodTypes = apiInfo.methodType;
                    apiInfo.url=key;
                } else {
                    apiInfo.methodTypes = apiInfo.methodTypes + "，delete";
                }
            }
            methodApis.push(apiInfo);
        }
        console.log(methodApis);
        return methodApis;

    }

    /***
     * li标签click事件
     */
    DApiUI.initLiClick=function () {
        DApiUI.getMenu().find(".menuLi").bind("click",function (e) {
            e.preventDefault();
            var that=$(this);
            var data=that.data("data");
            DApiUI.log("Li标签click事件");
            DApiUI.log(data);
            //获取parent-Li的class属性值
            var parentLi=that.parent().parent();
            DApiUI.log(parentLi);
            var className=parentLi.prop("class");
            DApiUI.log(className)
            DApiUI.getMenu().find("li").removeClass("active");
            //parentLi.addClass("active");
            that.addClass("active");
            DApiUI.createApiInfoTable(data);
            DApiUI.createDebugTab(data);
            DApiUI.createResponseTab(data);
        })
    }

    DApiUI.getStringValue=function (obj) {
        var str="";
        if(typeof (obj)!='undefined'&&obj!=null){
            str=obj.toString();
        }
        return str;
    }

    DApiUI.createResponseTab=function (apiInfo) {

        var table = $('<table class="table table-hover table-bordered table-text-center"></table>');
        var thead = $('<thead><tr><th colspan="2" style="text-align:center">接口响应</th></tr></thead>');
        table.append(thead);
        var tbody = $('<tbody></tbody>');

        var summary = $('<tr><th class="active" style="text-align: right;width:10%;">接口名称</th><td style="text-align: left">'
            + DApiUI.getStringValue(apiInfo.summary) + '</td></tr>');
        tbody.append(summary);

        var description = $('<tr><th class="active" style="text-align: right;">接口描述</th><td style="text-align: left">'
            + DApiUI.getStringValue(apiInfo.description) + '</td></tr>');
        tbody.append(description);

        //请求参数
        var args = $('<tr><th class="active" style="text-align: right;">响应参数</th></tr>');
        //rowspan
        //判断是否有请求参数
        var resp=apiInfo.responses;
        if(resp.hasOwnProperty("200")){
            var ok=resp["200"];
            if(ok.hasOwnProperty("schema")){
                var schema=ok["schema"];
                var ref=schema["$ref"];
                var regex=new RegExp("#/definitions/(.*)$","ig");
                if(regex.test(ref)) {
                    var refType = RegExp.$1;
                    var definitionMap=DApiUI.getDoc().data("definitionMap");
                    if(definitionMap.hasOwnProperty(refType)){
                        var refObjct = definitionMap[refType];
                        DApiUI.log("hahahahah" + JSON.stringify(refObjct));
                        var refObjectPropLen = Object.getOwnPropertyNames(refObjct).length;
                        DApiUI.log(refObjectPropLen);
                        var ptd = $("<td></td>");
                        var ptable = $('<table class="table table-bordered"></table>')
                        var phead = $('<thead><th>SCHEMA</th><th>参数名称</th><th>参数类型</th><th>参数描述</th></thead>');
                        ptable.append(phead);
                        var pbody = $('<tbody></tbody>');
                        var refObjctArray = new Array();
                        for(var refProp in refObjct){
                            if(refProp=='objKey'){
                                continue;
                            }
                            var refObj = refObjct[refProp];
                            var type = "";
                            var desc = refProp;
                            if(typeof (refObj) != 'undefined' && refObj != null){
                                if(refObj.hasOwnProperty('type')){
                                    type = refObj.type;
                                } else if(refObj.hasOwnProperty('$ref')){
                                    var refModel = refObj.$ref;
                                    refModel = DApiUI.getDefinitionModel(refModel);
                                    type = 'ref => ' + refModel;
                                    if(definitionMap.hasOwnProperty(refModel)){
                                        var refModelObj = definitionMap[refModel];
                                        refObjctArray.push(refModelObj);
                                    }
                                }
                                if(refObj.hasOwnProperty('description')){
                                    desc = refObj.description;
                                }
                            }
                            var ptr = $('<tr><td>' + refProp + '</td><td>'
                                + type + '</td><td>' + desc + '</td>');
                            pbody.append(ptr);
                        }
                        pbody.find('tr').first().prepend('<td rowspan="' + (refObjectPropLen-1) +'">' + refType + '</td>');
                        for(var i=0; i < refObjctArray.length; i++){
                            DApiUI.responsParam(pbody, refObjctArray[i]);
                        }
                        ptable.append(pbody);
                        ptd.append(ptable);
                        args.append(ptd);
                    }
                }
            }
        }

        tbody.append(args);
        //响应数据结构
        var responseConstruct = $('<tr><th class="active" style="text-align: right;">响应示例</th></tr>');
        var responseConstructtd = $('<td  style="text-align: left"></td>')
        responseConstructtd.append(DApiUI.createResponseDefinition(apiInfo));
        responseConstruct.append(responseConstructtd);

        tbody.append(responseConstruct)

        //响应状态码
        //2017-05-05 yiyuanwei 修改响应状态说明
        var response = $('<tr><th class="active" style="text-align: right;">响应状态</th></tr>');
        if (typeof (apiInfo.responses) != 'undefined'
            && apiInfo.responses != null) {
            var resp = apiInfo.responses;
            var ptd = $("<td></td>");
            var ptable = $('<table class="table table-bordered"></table>')
            //var phead = $('<thead><th>状态码</th><th>说明</th><th>schema</th></thead>');
            var phead = $('<thead><th>状态值</th><th>说明</th></thead>');
            ptable.append(phead);
            var pbody = $('<tbody></tbody>');
//			if (resp.hasOwnProperty("200")) {
//				var ptr = $('<tr><td>200</td><td>http响应成功</td><td></td></tr>');
//				pbody.append(ptr);
//			}
            pbody.append('<tr><td>sucess</td><td>http响应成功</td></tr>');
            pbody.append('<tr><td>fail</td><td>http响应失败</td></tr>');
//			//400
//			pbody.append($('<tr><td>400</td><td>Bad Request 请求出现语法错误,一般是请求参数不对</td><td></td></tr>'));
//			//404
//			pbody.append($('<tr><td>404</td><td>Not Found 无法找到指定位置的资源</td><td></td></tr>'));
//			//401
//			pbody.append($('<tr><td>401</td><td>Unauthorized 访问被拒绝</td><td></td></tr>'));
//			//403
//			pbody.append($('<tr><td>403</td><td>Forbidden 资源不可用</td><td></td></tr>'));
//			//500
//			pbody.append($('<tr><td>500</td><td>服务器内部错误,请联系Java后台开发人员!!!</td><td></td></tr>'));
            ptable.append(pbody);
            ptd.append(ptable);
            response.append(ptd);
        } else {
            response.append($("<td>暂无</td>"));
        }
        tbody.append(response);
        table.append(tbody);

        //DApiUI.creatabTab();
        //内容覆盖
        //DApiUI.getDoc().html("");
        //查找接口doc
        DApiUI.getDoc().find("#tab3").find(".panel-body").html("")
        DApiUI.getDoc().find("#tab3").find(".panel-body").append(table);
        //DApiUI.getDoc().append(table);
    }

    DApiUI.getDefinitionModel = function(dm){
        return dm.replace("#/definitions/","");
    }


    DApiUI.responsParam = function(pbody, refObjct) {
        var index = 0;
        var refObjctArray = new Array();
        for (var refProp in refObjct) {
            if(refProp=='objKey'){
                continue;
            }
            var refObj = refObjct[refProp];
            var type = "";
            var desc = refProp;
            if (typeof (refObj) != 'undefined' && refObj != null) {
                if (refObj.hasOwnProperty('type')) {
                    type = refObj.type;
                } else if (refObj.hasOwnProperty('$ref')) {
                    var refModel = refObj.$ref;
                    refModel = DApiUI.getDefinitionModel(refModel);
                    type = 'ref => ' + refModel;
                    if(DApiUI.getDoc().data("definitionMap").hasOwnProperty(refModel)){
                        var refModelObj = DApiUI.getDoc().data("definitionMap")[refModel];
                        refObjctArray.push(refModelObj);
                    }
                }
                if (refObj.hasOwnProperty('description')) {
                    desc = refObj.description;
                }
            }
            var ptr = $('<tr><td>' + refProp + '</td><td>' + type + '</td><td>'
                + desc + '</td>');
            if(index == 0){
                ptr.prepend('<td rowspan="' + (Object.getOwnPropertyNames(refObjct).length-1) + '">' + refObjct.objKey + '</td>');
            }
            pbody.append(ptr);
            index++;
        }
        for(var i=0; i < refObjctArray.length; i++){
            DApiUI.responsParam(pbody, refObjctArray[i]);
        }
    }


    /**
     * 创建调试面板
     */
    DApiUI.createDebugTab=function (apiInfo) {
        DApiUI.log("创建调试tab")
        //方法、请求类型、发送按钮
        var div=$('<div style="width: 100%;margin: 0px auto;margin-top: 20px;"></div>');
        var headdiv1=$('<div class="input-group m-bot15"><span class="input-group-btn"><button class="btn btn-default btn-info" type="button">'+DApiUI.getStringValue(apiInfo.methodType)+'</button></span><input type="text" id="txtreqUrl" class="form-control" value="'+DApiUI.getStringValue(apiInfo.url)+'"/><span class="input-group-btn"><button id="btnRequest" class="btn btn-default btn-primary" type="button"> 发 送 </button></span></div>');
        div.append(headdiv1);


        //请求参数
        var divp=$('<div class="panel panel-primary"><div class="panel-heading">请求参数</div></div>')

        var divpbody=$('<div class="panel-body"></div>')
        //判断是否有请求参数
        if(typeof (apiInfo.parameters)!='undefined'&&apiInfo.parameters!=null){
            var table=$('<table class="table table-hover table-bordered table-text-center"></table>')
            var thead=$('<thead><tr><th></th><th>参数名称</th><th>参数值</th><th>操作</th></tr></thead>');
            table.append(thead);
            var tbody=$('<tbody id="paramBody"></tbody>');
            $.each(apiInfo.parameters,function (i, param) {
                var tr=$('<tr></tr>');
                tr.data("data",param);
                var checkbox=$('<td><div class="checkbox"><label><input type="checkbox" value="" checked></label></div></td>');
                var key=$('<td><input class="form-control p-key" value="'+param.name+'"/></td>')
                var value=$('<td><input class="form-control p-value" data-apiUrl="'+apiInfo.url+'" data-name="'+param.name+'" placeholder="'+DApiUI.getStringValue(param['description'])+'"/></td>');
                var oper=$('<td><button class="btn btn-danger btn-circle btn-lg" type="button"><strong>×</strong></button></td>');
                //删除事件
                oper.find("button").on("click",function (e) {
                    e.preventDefault();
                    var that=$(this);
                    that.parent().parent().remove();
                })
                //判断参数类型,针对path参数
                if(param["in"]=="path"){
                    //赋予change事件
                    value.find("input").on("keyup",function () {
                        var t=$(this);
                        var name=t.data("name");
                        var apiUrl=t.attr("data-apiUrl");
                        var realValue=apiUrl.replace("{"+name+"}",t.val());
                        //查找是否还存在其他path参数
                        $("#paramBody").find("tr").each(function (i, itr) {
                            var itrthat=$(this);
                            var itrdata=itrthat.data("data");
                            var itrname=itrdata["name"];
                            if(itrdata["in"]=="path"&&itrdata["name"]!=name){
                                //查找value值
                                var itrtdvalue=itrthat.find(".p-value").val();
                                if(itrtdvalue!=""){
                                    realValue=realValue.replace("{"+itrname+"}",itrtdvalue);
                                }
                            }
                        })
                        DApiUI.log(realValue);
                        $("#txtreqUrl").val(realValue);
                        DApiUI.log("keyup。。。。")
                    })

                }
                tr.append(checkbox).append(key).append(value).append(oper);
                tbody.append(tr);
            })
            table.append(tbody);
            divpbody.append(table);
        }else{
            divpbody.append($('<strong>暂无参数</strong>'))
        }
        divp.append(divpbody);


        div.append(divp);

        //创建reesponsebody
        var respcleanDiv=$('<div id="responsebody"></div>');
        div.append(respcleanDiv);

        DApiUI.getDoc().find("#tab2").find(".panel-body").html("")
        DApiUI.getDoc().find("#tab2").find(".panel-body").append(div);




        //发送事件
        headdiv1.find("#btnRequest").bind("click",function (e) {
            e.preventDefault();
            respcleanDiv.html("")
            DApiUI.log("发送请求");
            //
            var params={};

            //获取参数
            var paramBody=DApiUI.getDoc().find("#tab2").find("#paramBody")
            DApiUI.log("paramsbody..")
            DApiUI.log(paramBody)
            //组装请求url
            var url=DApiUI.getStringValue(apiInfo.url);
            var cacheData=DApiUI.getDoc().data("data");
            if(typeof (cacheData.basePath)!="undefined"&&cacheData.basePath!=""){
                if(cacheData.basePath!="/"){
                    DApiUI.log("NOT ROOT PATH:");
                    url=cacheData.basePath+DApiUI.getStringValue(apiInfo.url);
                }
            }


            paramBody.find("tr").each(function () {
                var paramtr=$(this);
                var cked=paramtr.find("td:first").find(":checked").prop("checked");
                DApiUI.log(cked)
                if (cked){
                    var trdata=paramtr.data("data");
                    //获取key
                    //var key=paramtr.find("td:eq(1)").find("input").val();
                    var key=trdata["name"];
                    //获取value
                    var value=paramtr.find("td:eq(2)").find("input").val();
                    if(trdata["in"]=="path"){
                        url=url.replace("{"+key+"}",value);
                    }else{
                        params[key]=value;
                    }
                    DApiUI.log("key:"+key+",value:"+value);
                }
            })
            DApiUI.log("获取参数..")
            DApiUI.log(params);
            DApiUI.log(apiInfo)

            DApiUI.log("请求url："+url);
            $.ajax({
                url:url,
                type:DApiUI.getStringValue(apiInfo.methodType),
                data:params,
                success:function (data,status,xhr) {
                    var resptab=$('<div id="resptab" class="tabs-container" ></div>')
                    var ulresp=$('<ul class="nav nav-tabs">' +
                        '<li class=""><a data-toggle="tab" href="#tabresp" aria-expanded="false"> 响应内容 </a></li>' +
                        '<li class=""><a data-toggle="tab" href="#tabcookie" aria-expanded="true"> Cookies</a></li>' +
                        '<li class=""><a data-toggle="tab" href="#tabheader" aria-expanded="true"> Headers </a></li></ul>')

                    resptab.append(ulresp);
                    var respcontent=$('<div class="tab-content"></div>');

                    var resp1=$('<div id="tabresp" class="tab-pane active"><div class="panel-body"><pre></pre></div></div>');
                    var resp2=$('<div id="tabcookie" class="tab-pane active"><div class="panel-body">暂无</div>');
                    var resp3=$('<div id="tabheader" class="tab-pane active"><div class="panel-body">暂无</div></div>');

                    respcontent.append(resp1).append(resp2).append(resp3);

                    resptab.append(respcontent)

                    respcleanDiv.append(resptab);
                    DApiUI.log(xhr);
                    DApiUI.log(xhr.getAllResponseHeaders());
                    var allheaders=xhr.getAllResponseHeaders();
                    if(allheaders!=null&&typeof (allheaders)!='undefined'&&allheaders!=""){
                        var headers=allheaders.split("\r\n");
                        var headertable=$('<table class="table table-hover table-bordered table-text-center"><tr><th>请求头</th><th>value</th></tr></table>');
                        for(var i=0;i<headers.length;i++){
                            var header=headers[i];
                            if(header!=null&&header!=""){
                                var headerValu=header.split(":");
                                var headertr=$('<tr><th class="active">'+headerValu[0]+'</th><td>'+headerValu[1]+'</td></tr>');
                                headertable.append(headertr);
                            }
                        }
                        //设置Headers内容
                        resp3.find(".panel-body").html("")
                        resp3.find(".panel-body").append(headertable);
                    }
                    var contentType=xhr.getResponseHeader("Content-Type");
                    DApiUI.log("Content-Type:"+contentType);
                    DApiUI.log(xhr.hasOwnProperty("responseJSON"))
                    if (xhr.hasOwnProperty("responseJSON")){
                        //如果存在该对象,服务端返回为json格式
                        resp1.find(".panel-body").html("")
                        DApiUI.log(xhr["responseJSON"])
                        var pre=$('<pre></pre>')
                        var jsondiv=$('<div></div>')
                        jsondiv.JSONView(xhr["responseJSON"]);
                        pre.html(JSON.stringify(xhr["responseJSON"],null,2));
                        resp1.find(".panel-body").append(jsondiv);
                    }else{
                        //判断content-type
                        //如果是image资源
                        var regex=new RegExp('image/(jpeg|jpg|png|gif)','g');
                        if(regex.test(contentType)){
                            var d=DApiUI.getDoc().data("data");
                            var imgUrl="http://"+d.host+apiInfo.url;
                            var img = document.createElement("img");
                            img.onload = function(e) {
                                window.URL.revokeObjectURL(img.src); // 清除释放
                            };
                            img.src = imgUrl;
                            resp1.find(".panel-body").html("")
                            resp1.find(".panel-body")[0].appendChild(img);
                        }else{
                            //判断是否是text
                            var regex=new RegExp('.*?text.*','g');
                            if(regex.test(contentType)){
                                resp1.find(".panel-body").html("")
                                resp1.find(".panel-body").html(xhr.responseText);
                            }
                        }

                    }

                    DApiUI.log("tab show...")
                    resptab.find("a:first").tab("show");
                },
                error:function (xhr, textStatus, errorThrown) {
                    DApiUI.log("error.....")
                    DApiUI.log(xhr);
                    DApiUI.log(textStatus);
                    DApiUI.log(errorThrown);
                    var resptab=$('<div id="resptab" class="tabs-container" ></div>')
                    var ulresp=$('<ul class="nav nav-tabs">' +
                        '<li class=""><a data-toggle="tab" href="#tabresp" aria-expanded="false"> 响应内容 </a></li>' +
                        '<li class=""><a data-toggle="tab" href="#tabcookie" aria-expanded="true"> Cookies</a></li>' +
                        '<li class=""><a data-toggle="tab" href="#tabheader" aria-expanded="true"> Headers </a></li></ul>')

                    resptab.append(ulresp);
                    var respcontent=$('<div class="tab-content"></div>');

                    var resp1=$('<div id="tabresp" class="tab-pane active"><div class="panel-body"><pre></pre></div></div>');
                    var resp2=$('<div id="tabcookie" class="tab-pane active"><div class="panel-body">暂无</div>');
                    var resp3=$('<div id="tabheader" class="tab-pane active"><div class="panel-body">暂无</div></div>');

                    respcontent.append(resp1).append(resp2).append(resp3);

                    resptab.append(respcontent)

                    respcleanDiv.append(resptab);
                    DApiUI.log(xhr);
                    DApiUI.log(xhr.getAllResponseHeaders());
                    var allheaders=xhr.getAllResponseHeaders();
                    if(allheaders!=null&&typeof (allheaders)!='undefined'&&allheaders!=""){
                        var headers=allheaders.split("\r\n");
                        var headertable=$('<table class="table table-hover table-bordered table-text-center"><tr><th>请求头</th><th>value</th></tr></table>');
                        for(var i=0;i<headers.length;i++){
                            var header=headers[i];
                            if(header!=null&&header!=""){
                                var headerValu=header.split(":");
                                var headertr=$('<tr><th class="active">'+headerValu[0]+'</th><td>'+headerValu[1]+'</td></tr>');
                                headertable.append(headertr);
                            }
                        }
                        //设置Headers内容
                        resp3.find(".panel-body").html("")
                        resp3.find(".panel-body").append(headertable);
                    }
                    var contentType=xhr.getResponseHeader("Content-Type");
                    DApiUI.log("Content-Type:"+contentType);
                    var jsonRegex="";
                    DApiUI.log(xhr.hasOwnProperty("responseJSON"))
                    if (xhr.hasOwnProperty("responseJSON")){
                        //如果存在该对象,服务端返回为json格式
                        resp1.find(".panel-body").html("")
                        DApiUI.log(xhr["responseJSON"])
                        var jsondiv=$('<div></div>')
                        jsondiv.JSONView(xhr["responseJSON"]);
                        resp1.find(".panel-body").append(jsondiv);
                    }else{
                        //判断是否是text
                        var regex=new RegExp('.*?text.*','g');
                        if(regex.test(contentType)){
                            resp1.find(".panel-body").html("")
                            resp1.find(".panel-body").html(xhr.responseText);
                        }
                    }
                    DApiUI.log("tab show...")
                    resptab.find("a:first").tab("show");

                }
            })
        })

    }

    DApiUI.createDebugResponseTab=function (parent, data) {

    }


    DApiUI.writeUTF8=function (str, isGetBytes) {
        var back = [],
            byteSize = 0;
        for (var i = 0; i < str.length; i++) {
            var code = str.charCodeAt(i);
            if (code >= 0 && code <= 127) {
                byteSize += 1;
                back.push(code);
            } else if (code >= 128 && code <= 2047) {
                byteSize += 2;
                back.push((192 | (31 & (code >> 6))));
                back.push((128 | (63 & code)))
            } else if (code >= 2048 && code <= 65535) {
                byteSize += 3;
                back.push((224 | (15 & (code >> 12))));
                back.push((128 | (63 & (code >> 6))));
                back.push((128 | (63 & code)))
            }
        }
        for (i = 0; i < back.length; i++) {
            if (back[i] > 255) {
                back[i] &= 255
            }
        }
        if (isGetBytes) {
            return back
        }
        if (byteSize <= 255) {
            return [0, byteSize].concat(back);
        } else {
            return [byteSize >> 8, byteSize & 255].concat(back);
        }
    }

    DApiUI.createApiInfoTable=function (apiInfo) {
        var table=$('<table class="table table-hover table-bordered table-text-center"></table>');
        var thead=$('<thead><tr><th colspan="2" style="text-align:center">接口请求说明</th></tr></thead>');
        table.append(thead);
        var tbody=$('<tbody></tbody>');

        var url=$('<tr><th class="active" style="text-align: right;">接口URL</th><td style="text-align: left"><code>http://'+ apiData.host+'/api/v1'+apiData.basePath+DApiUI.getStringValue(apiInfo.url)+'</code></td></tr>');
        tbody.append(url);

        var summary=$('<tr><th class="active" style="text-align: right;">接口名称</th><td style="text-align: left">'+DApiUI.getStringValue(apiInfo.summary)+'</td></tr>');
        tbody.append(summary);


        var description=$('<tr><th class="active" style="text-align: right;">接口描述</th><td style="text-align: left">'+DApiUI.getStringValue(apiInfo.description)+'</td></tr>');
        tbody.append(description);

        var methodType=$('<tr><th class="active" style="text-align: right;">请求方式</th><td style="text-align: left"><code>'+DApiUI.getStringValue(apiInfo.methodTypes)+'</code></td></tr>');
        tbody.append(methodType);


        var consumes=$('<tr><th class="active" style="text-align: right;">consumes</th><td style="text-align: left"><code>'+apiInfo.consumes.join(",")+'</code></td></tr>');
        tbody.append(consumes);

        var produces=$('<tr><th class="active" style="text-align: right;">produces</th><td style="text-align: left"><code>'+apiInfo.produces.join(",")+'</code></td></tr>');
        tbody.append(produces);

        //请求参数
        var args=$('<tr><th class="active" style="text-align: right;">请求参数</th></tr>');
        //判断是否有请求参数
        if(typeof (apiInfo.parameters)!='undefined'&&apiInfo.parameters!=null){
            var ptd=$("<td></td>");
            var ptable=$('<table class="table table-bordered"></table>')
            var phead=$('<thead><th>SCHEMA</th><th>参数名称</th><th>参数类型</th><th>输入模式</th><th>是否必须</th><th>参数描述</th></thead>');
            ptable.append(phead);
            var pbody=$('<tbody></tbody>');
            var paramRefArray = [];
            $.each(apiInfo.parameters,function (i, param) {
                //判断是否有type属性,如果有,则后端为实体类形参
                var ptype="string";
                if(param.hasOwnProperty("type")){
                    ptype=param["type"];
                    var ptr=$('<tr><td></td><td>'+param.name+'</td><td>'+ptype+'</td><td>'+DApiUI.getStringValue(param['in'])+'</td><td>'+param['required']+'</td><td>'+DApiUI.getStringValue(param['description'])+'</td></tr>');
                    pbody.append(ptr);
                }else{
                    ///判断是有schma
                    if(param.hasOwnProperty("schema")){
                        var schema=param["schema"];
                        //是否有type
                        if(schema.hasOwnProperty("type")){
                            ptype=schema["type"];
                        }
//                        if(schema.hasOwnProperty("$ref")){
//                        	var refStr = schema['$ref'];
//                        	refStr = DApiUI.getDefinitionModel(refStr);
//                        	paramRefArray.push(DApiUI.getDoc().data("definitionMap")[refStr]);
//                        }
                    }
                }
            })
//            for(var i = 0; i < paramRefArray.length; i++){
//            	DApiUI.createRequestDefinition(pbody, paramRefArray[i])
//            }
            ptable.append(pbody);
            ptd.append(ptable);
            args.append(ptd);
        }else{
            args.append($('<td  style="text-align: left">暂无</td>'));
        }
        tbody.append(args);

        table.append(tbody);
        DApiUI.creatabTab();
        //内容覆盖
        //DApiUI.getDoc().html("");
        //查找接口doc
        DApiUI.getDoc().find("#tab1").find(".panel-body").html("")
        DApiUI.getDoc().find("#tab1").find(".panel-body").append(table);
        //DApiUI.getDoc().append(table);
    }

    DApiUI.createRequestDefinition=function (pbody, paramObj) {
        var index = 0;
        var refObjctArray = new Array();
        for (var refProp in paramObj) {
            if(refProp=='objKey'){
                continue;
            }
            var refObj = paramObj[refProp];
            var type = "";
            var desc = refProp;
            if (typeof (refObj) != 'undefined' && refObj != null) {
                if (refObj.hasOwnProperty('type')) {
                    type = refObj.type;
                } else if (refObj.hasOwnProperty('$ref')) {
                    var refModel = refObj.$ref;
                    refModel = DApiUI.getDefinitionModel(refModel);
                    type = 'ref => ' + refModel;
                    if(DApiUI.getDoc().data("definitionMap").hasOwnProperty(refModel)){
                        var refModelObj = DApiUI.getDoc().data("definitionMap")[refModel];
                        refObjctArray.push(refModelObj);
                    }
                }
                if (refObj.hasOwnProperty('description')) {
                    desc = refObj.description;
                }
            }
            var ptr=$('<tr><td>'+refProp+'</td><td>'+type+'</td><td>'+"path|body|url"+'</td><td>'+true+'</td><td>'+desc+'</td></tr>');
            if(index == 0){
                ptr.prepend('<td rowspan="' + (Object.getOwnPropertyNames(paramObj).length-1) + '">' + paramObj.objKey + '</td>');
            }
            pbody.append(ptr);
            index++;
        }
        for(var i=0; i < refObjctArray.length; i++){
            DApiUI.createRequestDefinition(pbody, refObjctArray[i]);
        }
    }

    DApiUI.createResponseDefinition=function (apiInfo) {
        var resp=apiInfo.responses;
        var div=$("<div class='panel'>暂无</div>")
        if(resp.hasOwnProperty("200")){
            var ok=resp["200"];
            if(ok.hasOwnProperty("schema")){
                var schema=ok["schema"];
                var ref=schema["$ref"];
                var refType = DApiUI.getDefinitionModel(ref);
//                var definitionsArray=DApiUI.getDoc().data("definitionsArray");
//                for(var i=0;i<definitionsArray.length;i++){
//                    var definition=definitionsArray[i];
//                    if(definition.key==refType){
//                        div.html("")
//                        div.JSONView(definition.value);
//                    }
//                }
                var definitionMap=DApiUI.getDoc().data("definitionOldMap");
                var resObj = definitionMap[refType];
                div.html("")
                div.JSONView(resObj);
            }
        }
        return div;
    }



    DApiUI.definitions=function (menu) {
        var definitionsArray=new Array();
        var definitionMap = {};
        var definitionOldMap = {};
        DApiUI.log("definitionsArray....")
        if(menu!=null&&typeof (menu)!="undefined"&&menu.hasOwnProperty("definitions")){
            var definitions=menu["definitions"];
            for(var definition in definitions){
                var defiType=new definitionType();
                defiType.key=definition;
                //获取value
                var value=definitions[definition];
                if (checkUndefined(value)){
                    //是否有properties
                    if(value.hasOwnProperty("properties")){
                        var properties=value["properties"];
                        var defiTypeValue={};
                        var defiTypeValueMap={};
                        var defiTypeValueOldMap={};
                        for(var property in properties){
                            var propobj=properties[property];
                            //默认string类型
                            var propValue="";
                            //判断是否有类型
                            if(propobj.hasOwnProperty("type")){
                                var type=propobj["type"];
                                if(checkIsBasicType(type)){
                                    propValue=getBasicTypeValue(type);
                                }else{
                                    if(type=="array"){
                                        propValue=new Array();
                                        var items=propobj["items"];
                                        var ref=items["$ref"];
                                        var regex=new RegExp("#/definitions/(.*)$","ig");
                                        if(regex.test(ref)){
                                            var refType=RegExp.$1;
                                            propValue.push(findRefDefinition(refType,definitions));
                                        }
                                    }
                                }

                            }else{
                                if(propobj.hasOwnProperty("$ref")){
                                    var ref=propobj["$ref"];
                                    var regex=new RegExp("#/definitions/(.*)$","ig");
                                    if(regex.test(ref)) {
                                        var refType = RegExp.$1;
                                        propValue=findRefDefinition(refType,definitions);
                                    }
                                }else{
                                    propValue={};
                                }
                            }
                            propobj['propValue'] = propValue;
                            defiTypeValueOldMap[property]=propValue;
                            defiTypeValue[property]=propValue;
                            defiTypeValueMap['objKey'] = definition;
                            defiTypeValueMap[property]=propobj;
                        }
                        defiType.value=defiTypeValue;
                    }else{
                        defiType.value={};
                    }
                }
                definitionOldMap[definition]=defiTypeValueOldMap;
                definitionMap[definition]=defiTypeValueMap;
                definitionsArray.push(defiType);
            }
        }
        DApiUI.getDoc().data("definitionsArray",definitionsArray);
        //model 当key
        DApiUI.getDoc().data("definitionMap", definitionMap);
        DApiUI.getDoc().data("definitionOldMap", definitionOldMap);
    }

    function checkIsBasicType(type) {
        var basicTypes=["string","integer","number","object","boolean"];
        var flag=false;
        if($.inArray(type,basicTypes)>-1){
            flag=true;
        }
        return flag;
    }

    function getBasicTypeValue(type) {
        var propValue="";
        //是否是基本类型
        if(type=="integer"){
            propValue=0;
        }
        if(type=="boolean"){
            propValue=true;
        }
        if(type=="object"){
            propValue={};
        }
        if(type=="number"){
            propValue=parseFloat(0);
        }
        return propValue;
    }

    function findRefDefinition(definitionName, definitions) {
        var defaultValue="";
        for(var definition in definitions){
            if(definitionName==definition){
                var value=definitions[definition];
                //是否有properties
                if(value.hasOwnProperty("properties")){
                    var properties=value["properties"];
                    var defiTypeValue={};
                    for(var property in properties){
                        var propobj=properties[property];
                        //默认string类型
                        var propValue="";
                        //判断是否有类型
                        if(propobj.hasOwnProperty("type")){
                            var type=propobj["type"];
                            if(checkIsBasicType(type)){
                                propValue=getBasicTypeValue(type);
                            }else{
                                if(type=="array"){
                                    propValue=new Array();
                                    var items=propobj["items"];
                                    var ref=items["$ref"];
                                    var regex=new RegExp("#/definitions/(.*)$","ig");
                                    if(regex.test(ref)){
                                        var refType=RegExp.$1;
                                        propValue.push(findRefDefinition(refType,definitions));
                                    }
                                }
                            }

                        }else if(propobj.hasOwnProperty("$ref")){

                        }
                        defiTypeValue[property]=propValue;
                    }
                    defaultValue=defiTypeValue;
                }else{
                    defaultValue={};
                }
            }
        }
        return defaultValue;
    }
    function checkUndefined(obj) {
        var flag=false;
        if(obj!=null&&typeof (obj)!="undefined"){
            flag=true;
        }
        return flag;
    }


    function definitionType() {
        this.key="";
        this.value={};
    }


    /***
     * 获取默认请求参数类型
     * @param obj
     * @returns {string}
     */
    DApiUI.getDefaultRequiredType=function (obj) {
        var t="string";
        if(typeof (obj)!='undefined'&&obj!=null){
            t=obj.toString();
        }
        return  t;
    }

    /***
     * 查找子类
     * @param tagInfo
     * @param menu
     */
    DApiUI.initChildrens=function (tagInfo, menu) {

    }

    DApiUI.getDoc=function () {
        return $("#content");
    }
    DApiUI.getMenu=function () {
        return $("#menu");
    }

    DApiUI.log=function (msg) {
        if (window.console){
            console.log(msg);
        }
    }
    DApiUI.init();


    /***
     * 标签组信息
     * @constructor
     */
    function TagInfo(name,description) {
        this.name=name;
        this.description=description;
        this.childrens=new Array();
    }


    /***
     * api实体信息
     * @param options
     * @constructor
     */
    function ApiInfo(options) {
        //判断options
        this.tag="";
        this.url="";
        this.description="";
        this.operationId="";
        this.parameters=new Array();
        this.produces=new Array();
        this.responses={};
        this.methodType="post";
        this.consumes=new Array();
        this.summary="";
        if(options!=null&& typeof (options)!='undefined' ){
            this.tag=options.tags[0];
            this.description=options.description;
            this.operationId=options.operationId;
            this.summary=options.summary;
            this.parameters=options.parameters;
            this.produces=options.produces;
            this.responses=options.responses;
            this.consumes=options.consumes;
        }
    }

})(jQuery)
