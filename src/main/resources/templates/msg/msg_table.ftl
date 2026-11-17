<#ftl output_format="HTML" auto_esc=true>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
    <style>
        body {
            margin: 0;
            color: #18263a;
            font: 18px/1.55 "Noto Sans CJK SC", "Microsoft YaHei", sans-serif;
        }
        .dict-card {
            box-sizing: border-box;
            width: 720px;
            padding: 30px;
            background: #f2f6fb;
        }
        h1 {
            margin: 0 0 20px;
            color: #17365d;
            font-size: 28px;
            overflow-wrap: anywhere;
        }
        table {
            width: 100%;
            table-layout: fixed;
            border-collapse: collapse;
            background: #fff;
        }
        th, td {
            padding: 12px 16px;
            border-bottom: 1px solid #dce6f2;
            text-align: left;
            overflow-wrap: anywhere;
        }
        th {
            background: #e3edf9;
            color: #244c7c;
        }
        td {
            vertical-align: top;
            white-space: pre-wrap;
        }
        th:first-child { width: 48px; }
        tbody tr:nth-child(even) { background: #f8fbff; }
        .empty { text-align: center; color: #4f5e72; }
        footer { margin-top: 18px; color: #4f5e72; font-size: 16px; }
    </style>
</head>
<body>
<main class="dict-card">
    <h1>${title}</h1>
    <table>
        <thead>
        <tr>
            <th scope="col">序号</th>
            <#list columns as column>
                <th scope="col">${column}</th>
            </#list>
        </tr>
        </thead>
        <tbody>
        <#list rows as row>
            <tr>
                <td>${row?counter}</td>
                <#list row as value>
                    <td>${value}</td>
                </#list>
            </tr>
        <#else>
            <tr><td class="empty" colspan="${columns?size + 1}">暂无记录</td></tr>
        </#list>
        </tbody>
    </table>
    <#if summary?has_content>
        <footer>${summary}</footer>
    </#if>
</main>
</body>
</html>
