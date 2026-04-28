<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Freemarker</title>
    <style>
        #span1 {
            color: red;
        }
        #app-container {
            /*    背景图*/
            background-image: url("/static/background.jpg");
            background-size: auto;
            background-repeat: no-repeat;
            height: 932px;
            width: 430px;
        }
    </style>
</head>
<body>
<div id="app-container">
    <span id="span1" >111</span>
    Hello 1111111${testId}!
</div>
</body>
</html>