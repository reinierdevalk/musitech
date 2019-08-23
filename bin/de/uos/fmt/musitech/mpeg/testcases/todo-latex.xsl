<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:output method="text" indent="no"/>
  <xsl:template match="/">
    \begin{document}
    <xsl:apply-templates/>
    \end{document}
  </xsl:template>
  <xsl:template match="requirement">
    \section*{<xsl:apply-templates select="@id"/><xsl:text> </xsl:text><xsl:apply-templates select="@name"/>}
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="aspect">
    \subsection*{<xsl:apply-templates select="../@id"/>.<xsl:apply-templates select="@id"/><xsl:text> </xsl:text><xsl:apply-templates select="@name"/>}
    \begin{tabular}{|p{.5\textwidth}|p{.5\textwidth}|}
      \hline
      \textbf{TODO} &amp; \textbf{Solution} \\
      \hline
      <xsl:apply-templates/>
    \end{tabular}
  </xsl:template>
  <xsl:template match="todo">
	<xsl:apply-templates select="text()"/> &amp; 
	<xsl:apply-templates select="solution"/> \\ 
	\hline
  </xsl:template>
</xsl:stylesheet>