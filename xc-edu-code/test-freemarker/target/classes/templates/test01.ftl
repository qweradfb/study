<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
    <table>
        <tr>
            <td>序号</td>
            <td>姓名</td>
            <td>年龄</td>
            <td>钱包</td>
        </tr>
         <#list stus
         as stu>
                <tr>
                    <td>${stu_index+1}</td>
                    <td>${stu.name}</td>
                    <td>${stu.age}</td>
                    <td>${stu.money}</td>
                </tr>
          </#list>

    </table>

    <hr>
    ${stuMap['stu1'].name}
    <hr>
    <#list stuMap?keys as k>
        <#if stuMap[k].name == '小明'>lala</#if>
       ${stuMap[k].name} <br>
        ${stuMap[k].age} <br>
    </#list>

    <#if stu1??>
        lala
    </#if>
    <#if stu??>
        lala
    </#if>
    <hr>
    ${(stu.bestFriend.name)!""}
    <hr>
    ${stu1.birthday?date}
    ${stu1.birthday?time}
    ${stu1.birthday?datetime}
    ${stu1.birthday?string('yyyy-MM-dd')}
</body>
</html>