<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="iso-8859-1" indent="no"/>
    
    <xsl:variable name="phone" select="'snom370'"/>

    <xsl:template match="SnomIPPhoneMenu">
        <html>
            <head>
                <xsl:call-template name="styles" />
                <title>
                    <xsl:value-of select="Title"/>
                </title>
            </head>
            <body>
                <div id="header">
                    <xsl:value-of select="Title"/>
                </div>
                <ol id="content">
                    <xsl:apply-templates select="MenuItem" />
                </ol>
                <xsl:call-template name="footer" />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="SnomIPPhoneDirectory">
        <html>
            <head>
                <xsl:call-template name="styles" />
                <title>
                    <xsl:value-of select="Title"/>
                </title>
            </head>
            <body>
                <div id="header">
                    <xsl:value-of select="Title"/>
                </div>
                <ol id="content">
                    <xsl:apply-templates select="DirectoryEntry" />
                </ol>
                <xsl:call-template name="footer" />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="SnomIPPhoneInput">
        <xsl:param name="def" select="InputItem/DefaultValue"/>
        <xsl:variable name="param" select="URL" />
        <html>
            <head>
                <xsl:call-template name="styles" />
                <title>
                    <xsl:value-of select="Title"/>
                </title>
            </head>
            <body>
                <div id="header">
                    <xsl:value-of select="InputItem/DisplayName"/>
                </div>
                <div id="content">
                    <form action="{$param}" method="get">
                        <textarea name="param">
                            <xsl:value-of select="InputItem/DefaultValue"/>
                        </textarea>
                        <br />
                        <input type="submit" value="Senden" />
                    </form>
                </div>
                <xsl:call-template name="footer" />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="SnomIPPhoneText">
        <html>
            <head>
                <xsl:call-template name="styles" />
                <title>
                    <xsl:value-of select="Title"/>
                </title>
            </head>
            <body>
                <div id="header">
                    <xsl:value-of select="Title"/>
                </div>
                <div id="content">
                    <xsl:value-of select="Text"/>
                </div>
                <xsl:call-template name="footer" />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="SnomIPPhoneImageFile">
        <xsl:param name="src" select="URL"/>
        <xsl:param name="x" select="LocationX"/>
        <xsl:param name="y" select="LocationY"/>
        <html>
            <head>  
                <xsl:call-template name="styles" />
                <title></title>
            </head>
            <body>
                <div id="header">
                    <xsl:value-of select="InputItem/DisplayName"/>
                </div>
                <xsl:call-template name="footer" />
                <div id="image_container">
                    <img style="left:{$x}px;top:{$y}px" src="{$src}" />
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="SnomIPPhoneImage">
        <xsl:param name="data" select="Data"/>
        <xsl:param name="x" select="LocationX"/>
        <xsl:param name="y" select="LocationY"/>
        <html>
            <head>  
                <xsl:call-template name="styles" />
                <title></title>
            </head>
            <body>
                <div id="header">
                    <xsl:value-of select="InputItem/DisplayName"/>
                </div>
                <xsl:call-template name="footer" />
                <div id="image_container">
                    <img style="left:{$x}px;top:{$y}px" src="data:image/png;base64,{$data}" />
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="MenuItem">
        <xsl:variable name="uri" select="URL" />
        <li>
            <a href="{$uri}">
                <xsl:value-of select="Name"/>
            </a>
        </li>
    </xsl:template>
    
    <xsl:template match="DirectoryEntry">
        <xsl:variable name="number" select="Telephone" />
        <li>
            <a href="tel:{$number}">
                <xsl:value-of select="Name"/>
            </a>
        </li>
    </xsl:template>
    
    <xsl:template match="SoftKeyItem" name="softkey">
        <xsl:param name="uri" select="URL" />
        <a href="{$uri}" class="softkey">
            <xsl:value-of select="Label"/>
        </a>
    </xsl:template>
    
    <xsl:template name="footer">
        <div id="footer">
            <xsl:for-each select="SoftKeyItem">
                <xsl:call-template name="softkey" />
            </xsl:for-each>
            <xsl:if test="count(SoftKeyItem) &lt; 1">
                <xsl:call-template name="softkey">
                    <xsl:with-param name="uri" select="'javascript:void(0);'" />
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="count(SoftKeyItem) &lt; 2">
                <xsl:call-template name="softkey">
                    <xsl:with-param name="uri" select="'javascript:void(0);'" />
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="count(SoftKeyItem) &lt; 3">
                <xsl:call-template name="softkey">
                    <xsl:with-param name="uri" select="'javascript:void(0);'" />
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="count(SoftKeyItem) &lt; 4">
                <xsl:call-template name="softkey">
                    <xsl:with-param name="uri" select="'javascript:void(0);'" />
                </xsl:call-template>
            </xsl:if>
        </div>    
    </xsl:template>
    
    <xsl:template name="styles">
        <style type="text/css">
* {
    font-family:monospace;
}

a{
    color:black;
    text-decoration:none;
    outline: none;
}

ol {
    margin:0;
    padding:0;
    list-style-type: ;
}

li {
    border-bottom:1px solid #a0a0a0;
    overflow:hidden;
    white-space:nowrap;
    counter-increment:listpos;
}

li:before{
    content: counter(listpos,decimal-leading-zero) " ";
}

li:hover,li:focus{
    background-color:#a8a8a8;
}

textarea{
    width:100%;
}

body {
    padding:0;
    margin:0;
    overflow:hidden;
    position:relative;
    margin:0 auto;
}

#header {
    font-weight:bold;
    border-bottom:2px solid black;
    background-color:#909090;
}

#content {
    font-size:large;
    overflow-x:hidden;
    overflow-y:scroll;
}

#footer {
    position:absolute;
    bottom:0;
    left:0;
    right:0;
    border-top:2px solid black;
    background-color:white;
}

#image_container {
    position:absolute;
    top:0;
    bottom:0px;
    left:0px;
    right:0px;
    pointer-events:none;
}

#image_container img {
    position:absolute;
}

.softkey {
    border-left:1px solid black;
    border-top:2px solid black;
    border-right:1px solid black;
    background-color:#909090;
    display:inline-block;
    text-align:center;
    overflow:hidden;
}
        </style>
        <xsl:if test="$phone='snom370'">
            <xsl:call-template name="css_snom370" />
        </xsl:if>
        <xsl:if test="$phone='snom360'">
            <xsl:call-template name="css_snom360" />
        </xsl:if>
    </xsl:template>
    <xsl:template name="css_snom360">
        <style type="text/css">
* {
    font-size:12px;
}

li {
    height:16px;
    line-height:16px;
}

textarea{
    height:108px
}

body {
    width:384px;
    height:192px;
}

#header {
    height:20px;
    line-height:20px;
    padding:0 3px;
}

#content {
    height:135px;
}

#footer {
    height:33px;
}


#image_container img {
    -moz-transform:translate(100%, 100%) scale(3);
    -ms-transform:translate(100%, 100%) scale(3);
    -o-transform:translate(100%, 100%) scale(3);
    -webkit-transform:translate(100%, 100%) scale(3);
    transform:translate(100%, 100%) scale(3)
}

.softkey {
    width:94px;
    height:20px;
    line-height:20px;
    margin-top:11px;
    -webkit-border-top-left-radius:4px;
    -moz-border-radius-topleft:4px;
    border-top-left-radius:4px;
    -webkit-border-top-right-radius:4px;
    -moz-border-radius-topright:4px;
    border-top-right-radius:4px;
}
        </style>
    </xsl:template>
    
    <xsl:template name="css_snom370">
        <style type="text/css">
* {
    font-size:18px;
}

li {
    height:27px;
    line-height:27px;
}

textarea{
    height:126px
}

body {
    width:480px;
    height:256px;
}

#header {
    height:26px;
    line-height:26px;
    padding:0 3px;
}

#content {
    height:166px;
}

#footer {
    background:white url('data:image/png;base64,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') 0 0 no-repeat;
    height:60px;
}


#image_container img {
    -moz-transform:translate(50%, 50%) scale(2);
    -ms-transform:translate(50%, 50%) scale(2);
    -o-transform:translate(50%, 50%) scale(2);
    -webkit-transform:translate(50%, 50%) scale(2);
    transform:translate(50%, 50%) scale(2)
}

.softkey {
    width:118px;
    height:24px;
    line-height:24px;
    margin-top:34px;
    -webkit-border-top-left-radius:5px;
    -moz-border-radius-topleft:5px;
    border-top-left-radius:5px;
    -webkit-border-top-right-radius:5px;
    -moz-border-radius-topright:5px;
    border-top-right-radius:5px;
}
        </style>
    </xsl:template>
</xsl:stylesheet>