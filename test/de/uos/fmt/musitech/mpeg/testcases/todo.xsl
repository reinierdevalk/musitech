<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:output method="html"/>
  <xsl:template match="/">
    <html><body>
    <xsl:apply-templates/>
    </body></html>
  </xsl:template>
  <xsl:template match="requirement">
    <h1 align="center"><xsl:apply-templates select="@id"/><xsl:text> </xsl:text><xsl:apply-templates select="@name"/></h1>
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="aspect">
    <h2><xsl:apply-templates select="../@id"/>.<xsl:apply-templates select="@id"/><xsl:text> </xsl:text><xsl:apply-templates select="@name"/></h2>
    <table border="1">
      <tr>
        <th align="left">TODO</th>
        <th align="left">Solution</th>
      </tr>
      <xsl:apply-templates/>
  </table>
  </xsl:template>
  <xsl:template match="todo">
    <tr>
      <td>
        <xsl:apply-templates select="text()"/>
      </td>
        <xsl:apply-templates select="solution"/>
    </tr>
  </xsl:template>
  <xsl:template match="solution">
  <td><xsl:apply-templates select="text()"/></td>
  </xsl:template>
</xsl:stylesheet>