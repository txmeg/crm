

<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <base href="<%=basePath%>">

    <title>test</title>
</head>
<body>
    $.ajax({

        url:"",
        data:{

        },
        type:"",
        dataType:"json",
        success:function(data){

            /*
                data:

            */

        }

    })

    $(".time").datetimepicker({
        minView: "month",
        language:  'zh-CN',
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true,
        pickerPosition: "bottom-left"
    });

</body>
</html>
