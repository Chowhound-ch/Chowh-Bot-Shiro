<#ftl output_format="HTML" auto_esc=true>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Epic 每周限免</title>
    <style>
        body {
            margin: 0;
            color: #edf3fa;
            font: 18px/1.55 "Noto Sans CJK SC", "Microsoft YaHei", sans-serif;
        }
        .epic-report {
            box-sizing: border-box;
            width: 820px;
            padding: 32px;
            background: #101a26;
        }
        header {
            padding: 22px 24px;
            border-left: 5px solid #66e3c0;
            background: #19293a;
        }
        h1, h2, h3, p { margin: 0; }
        h1 { font-size: 38px; }
        section { margin-top: 26px; }
        h2 {
            display: flex;
            align-items: baseline;
            justify-content: space-between;
            margin-bottom: 12px;
            color: #81e8cb;
            font-size: 24px;
        }
        h2 span { color: #b9c8d8; font-size: 16px; font-weight: 400; }
        .upcoming h2 { color: #f7cc85; }
        .game-card {
            display: grid;
            grid-template-columns: 220px minmax(0, 1fr);
            align-items: start;
            gap: 20px;
            padding: 18px;
            margin-top: 12px;
            background: #1b2a3b;
            border: 1px solid #32475c;
            border-radius: 12px;
        }
        .cover {
            display: grid;
            place-items: center;
            width: 100%;
            aspect-ratio: 16 / 10;
            overflow: hidden;
            border-radius: 8px;
            background: #102031;
            color: #b9c8d8;
        }
        .cover img { display: block; width: 100%; height: 100%; object-fit: contain; }
        .details { min-width: 0; overflow-wrap: anywhere; }
        h3 { font-size: 24px; line-height: 1.3; }
        dl {
            display: grid;
            grid-template-columns: max-content 1fr;
            gap: 3px 12px;
            margin: 11px 0 0;
            font-size: 16px;
        }
        dt { color: #b9c8d8; }
        dd { margin: 0; }
        .description {
            margin-top: 12px;
            padding-top: 11px;
            border-top: 1px solid #32475c;
            color: #cfdae6;
            font-size: 16px;
            white-space: pre-wrap;
        }
        .empty {
            margin-top: 22px;
            padding: 24px;
            background: #1b2a3b;
            color: #b9c8d8;
        }
    </style>
</head>
<body>
<#macro gameSection title games style>
    <#if games?has_content>
        <section class="${style}">
            <h2>${title}<span>${games?size} 款</span></h2>
            <#list games as game>
                <article class="game-card">
                    <div class="cover">
                        <#if game.cover?has_content>
                            <img src="${game.cover}" alt="${game.title}">
                        <#else>
                            <span>暂无封面</span>
                        </#if>
                    </div>
                    <div class="details">
                        <h3>${game.title}</h3>
                        <dl>
                            <#if game.price?has_content><dt>原价</dt><dd>${game.price}</dd></#if>
                            <#if game.seller?has_content><dt>发行商</dt><dd>${game.seller}</dd></#if>
                            <dt>领取开始</dt><dd>${game.start}</dd>
                            <dt>领取截止</dt><dd>${game.end}</dd>
                        </dl>
                        <#if game.description?has_content>
                            <p class="description">${game.description}</p>
                        </#if>
                    </div>
                </article>
            </#list>
        </section>
    </#if>
</#macro>
<main class="epic-report">
    <header><h1>Epic 每周限免</h1></header>
    <@gameSection title="正在限免" games=current style="current" />
    <@gameSection title="即将限免" games=upcoming style="upcoming" />
    <#if !current?has_content && !upcoming?has_content>
        <p class="empty">暂无可显示的限免游戏</p>
    </#if>
</main>
</body>
</html>
