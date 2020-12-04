<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){

		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#addBtn").click(function() {
			//alert("hello")
			/*

				操作模态窗口的方式：
					需要操作的模态窗口的jquery对象，调用modal方法，为该方法传递参数
					show：打开模态窗口
					hide：关闭模态窗口

			 */

			//$("#createActivityModal").modal("show")

			//走后台，目的是为了取得用户信息列表，为所有者下拉框赋值
			$.ajax({
				url:"workbench/activity/getUserList.do",
				type:"get",
				datatype:"json",
				success:function(data){
					//alert(data)
					/*
						data:
							[{"id":?,"name":?,....},{},{}]
					 */

					var html = "<option></option>";

					//TypeError: invalid 'in' operand "x"
					var obj= $.parseJSON(data);

					//e就是一个user对象
					$.each(obj,function(q,n){

						html += "<option value='"+n.id+"'>"+n.name+"</option>"//实际添加的是id，主键具有唯一性

					})

					$("#create-owner").append(html);

					//取得当前登录用户的id
					//在js中使用el表达式，el表达式一定要套用在字符串中
					var id = "${user.id}"
					$("#create-owner").val(id)

					$("#createActivityModal").modal("show");

				}
			});

		});

		$("#save").click(function(){

			$.ajax({
				url:"workbench/activity/save.do",
				data:{

					"owner":$.trim($("#create-owner").val()),
					"name":$.trim($("#create-name").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-description").val())

				},
				type:"post",
				dataType:"json",
				success:function(data){

					/*
						data
							{"success":true/false}
					*/

					if (data.success){

						//添加成功后
						//刷新市场活动信息列表（局部刷新）

						/*
							注意：
								我们拿到了form表单的jquery对象，
								对于表单的jquery对象，提供了submit（）方法让我么提交表单
								但是表单的jquery对象，没有为我们提供reset（）方法让我们重置表单（但是idea有提示由reset（）方法）

								虽然jquery对象没有为我们提供reset方法，但是原生js为我们提供了reset方法
								所以要将jquery对象转换为原生dom对象

								jquery对象转换为dom对象
									jquery对象【下标】
								dom对象转换为jquery对象
									$(dom)
						 */

						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						$("#activityAddFrom")[0].reset();

						//关闭添加操作模态窗口
						$("#createActivityModal").modal("hide");

						alert("添加市场活动成功");

					}else {

						alert("添加市场活动失败");

					}

				}
			})

		})

		//页面加载完毕触发一个方法
		//默认绽开列表的第一页，每页展现两条记录
		pageList(1,2);

		$("#searchBtn").click(function(){

			$("#hidden-name").val($("#search-name").val());
			$("#hidden-owner").val($("#search-owner").val());
			$("#hidden-startDate").val($("#search-startDate").val());
			$("#hidden-endDate-").val($("#search-endDate").val());

			pageList(1,2);

		})

		//为全选框的复选框绑定事件，触发全选操作
		$("#qx").click(function(){
			$("input[name=xz]").prop("checked",this.checked)
		})

		//这种做法是不行的
		/*$("input[name=xz]").click(function(){
			alert(123)
		})*/

		//动态生成的元素，是不能够以普通绑定事件的形式来进行操作的
		/*
			动态生成的元素，我们要以on方法的形式来触发事件

			语法：
				$(需要绑定元素的 有效的外层 元素).on(绑定事件的方式，需要绑定的元素的jquery对象，回调函数)
		*/
        $("#activityBody").on("click",$("input[name=xz]"),function(){
            // alert(123)

            $("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length)

        })

		$("#deleteBtn").click(function () {

			// alert($("input[name=xz]:checked").length)
			var $xz = $("input[name=xz]:checked");

			if ($xz.length==0){

				alert("请选择需要删除的记录")

			}else{

				// alert(23)

				if (confirm("确定要删除选中的记录吗")){

					//拼接参数
					var param = "";

					for (var i = 0;i < $xz.length;i++){

						param += "id=" + $($xz[i]).val();

						if (i < $xz.length - 1){

							param += "&";

						}

					}

					//alert(param)

					$.ajax({
						url:"workbench/activity/delete.do",
						data:param,
						type:"post",
						dataType:"json",
						success:function (data) {

							/*
                                data:
                                    {"success":true/false}
                            */

							if (data.success){

								alert("删除记录成功")
								//pageList(1,2);
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

							}else{

								alert("删除记录失败")

							}

						}
					})

				}

			}

		})

        $("#editBtn").click(function(){

            var $id = $("input[name=xz]:checked");

            if ($id.length == 0){

                alert("请选择需要修改的记录")

            }else if ($id.length > 1){

                alert("只能同时修改一条记录")

            }else {

                $.ajax({

                    url:"workbench/activity/getUserListAndActivity.do",
                    data:{
                    	"id":$id.val()
						},
                    type:"post",
                    dataType:"json",
                    success:function(data){

                        /*
                            data：
                                {"userList":[{},{},{}],"activity":{}}
                         */

                        var html = "<option></opiton>";

                        $.each(data.userList,function (i,e) {

                            html += "<option value='"+ e.id +"'>"+ e.name +"</option>";

                        })

                        $("#edit-owner").html(html);

                        $("#edit-id").val(data.activity.id);
                        $("#edit-name").val(data.activity.name);
						$("#edit-owner").val(data.activity.owner);
                        $("#edit-startDate").val(data.activity.startDate);
                        $("#edit-endDate").val(data.activity.endDate);
                        $("#edit-cost").val(data.activity.cost);
                        $("#edit-description").val(data.activity.description);

                        $("#editActivityModal").modal("show");

                    }

                })

            }

        })

		/*
			为了更新按钮绑定事件，执行市场活动的修改操作

			在实际开发过程中，一定是按照先做添加，在做修改的这种顺序
			所以，为了节省开发时间，修改操作一般都是copy添加操作
		*/

		$("#updateBtn").click(function () {

			$.ajax({
				url:"workbench/activity/update.do",
				data:{

					"id":$.trim($("#edit-id").val()),
					"owner":$.trim($("#edit-owner").val()),
					"name":$.trim($("#edit-name").val()),
					"startDate":$.trim($("#edit-startDate").val()),
					"endDate":$.trim($("#edit-endDate").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-description").val())

				},
				type:"post",
				dataType:"json",
				success:function(data){

					/*
						data
							{"success":true/false}
					*/

					if (data.success){

						//添加成功后
						//刷新市场活动信息列表（局部刷新）

						//刷新分页列表
						//pageList(1,2);

						/*
							$("#activityPage").bs_pagination('getOption', 'currentPage')
								操作后停留在当前页

							$("#activityPage").bs_pagination('getOption', 'rowsPerPage')
								操作后维持已经设置好的每页展现的记录数


						*/

						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//关闭添加操作模态窗口
						$("#editActivityModal").modal("hide");

						alert("修改市场活动成功");

					}else {

						alert("修改市场活动失败");

					}

				}
			})

		})

	});

	/*
		对于所有的关系型数据库，做前端的分页相关操作的基础组件
		就是pageNo和pageSize
		pageNo：页码
		pageSize：每页展现的记录数

		pageList方法：就是发出ajax请求到后台，从后台取得最新的市场活动信息列表数据
						通过响应回来的数据，局部刷新市场活动信息列表

		我们都在哪些情况下，需要调用pageListf方法（什么情况下需要刷新一下市场活动那个列表）
		（1）点击左侧菜单中的”市场活动“超链接，需要刷新市场活动列表，调用pageList方法
		（2）添加，修改，删除后，需要刷新市场活动列表，调用pageList方法
		（3）点击查询按钮的时候，需要刷新市场活动列表，需要调用pageList方法
		（4）点击分页组建的时候，调用pageList方法

		以上为pageList方法制定了六个入口 ，也就是说，在以上6个操作执行完毕后，我们必须要调用pageList方法，刷新市场活动信息列表
	*/
	function pageList(pageNo,pageSize){

		//将全选的复选框的✔去掉
		$("#qx").prop("checked",false)


		$("#search-name").val($("#hidden-name").val());
		$("#search-owner").val($("#hidden-owner").val());
		$("#search-startDate").val($("#hidden-startDate").val());
		$("#search-endDate").val($("#hidden-endDate").val());

		//alert("分页查询刷新")

		$.ajax({
			url:"workbench/activity/pageList.do",
			data: {

				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
				"startDate":$.trim($("#search-startDate").val()),
				"endDate":$.trim($("#search-endDate").val())

			},
			type:"get",
			dataType: "json",
			success:function(data){

				/*
					data:
						我们需要的，市场活动信息列表
						【{市场活动1}，{2}，{3}。。】
						{"total":100}

						{"total":100,"dataList":【{市场活动1}，{2}，{3}。。】}
				 */
				// alert(data)
				var html = "";

				$.each(data.dataList,function(i,n){

					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value="'+ n.id +'" /></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+ n.id +'\';">'+ n.name +'</a></td>';
					html += '<td>'+ n.owner +'</td>';
					html += '<td>'+ n.startDate +'</td>';
					html += '<td>'+ n.endDate +'</td>';
					html += '</tr>';

				})

				$("#activityBody").html(html);

				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;


                $("#activityPage").bs_pagination({

                    currentPage: pageNo, // 页码
                    rowsPerPage: pageSize, // 每页显示的记录条数
                    maxRowsPerPage: 20, // 每页最多显示的记录条数
                    totalPages: totalPages, // 总页数
                    totalRows: data.total, // 总记录条数

                    visiblePageLinks: 3, // 显示几个卡片

                    showGoToPage: true,
                    showRowsPerPage: true,
                    showRowsInfo: true,
                    showRowsDefaultInfo: true,

                    onChangePage : function(event, data){
                        pageList(data.currentPage , data.rowsPerPage);
                    }
                });

			}
		})

	}
	
</script>
</head>
<body>

	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="activityAddFrom">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="save" >保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

                        <input type="hidden" id="edit-id"/>
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">



								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">

                                <%--
                                    关于文本域textarea
                                        1）一定要以标签对的形式来呈现，正常状态下标签对要紧紧的挨着
                                        2）textarea虽然是以标签对的形式呈现的，但是她也是属于表单元素的范畴
                                            我们所有的对于textarea的取值和赋值操作，应该统一使用val（）方法（而不是html()方法）
                                --%>

								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">

					<%--
						data-toggle=”modal“：
							表示出发该按钮，将要打开一个模态窗口

						data-target="#createActivityModal"
							表示要打开哪个模态窗口，通过#id的形式找到该窗口

						现在我们是以属性和属性值的方式写在了button元素中，用来打开模态窗口
						但是这样做是有问题的。
							问题在于没有办法对按钮的功能进行扩充

						所以未来的实际项目开发，对于出发模态窗口的操作，一定不要写死在元素当中，
						应该由我们自己写js代码来操作
					--%>

				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">

				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>