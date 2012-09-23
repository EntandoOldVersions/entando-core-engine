INSERT INTO `sysconfig_xxx` (version, item, descr, config) VALUES ('test', 'contentTypes', 'Definition of the Content Types', '<?xml version="1.0" encoding="UTF-8"?>
<contenttypes>
	<contenttype typecode="ART" typedescr="Articolo rassegna stampa" viewpage="contentview" listmodel="11" defaultmodel="1">
		<attributes>
			<attribute name="Titolo" attributetype="Text" indexingtype="text">
				<validations>
					<required>true</required>
				</validations>
			</attribute>
			<list name="Autori" attributetype="Monolist">
				<nestedtype>
					<attribute name="Autori" attributetype="Monotext" />
				</nestedtype>
			</list>
			<attribute name="VediAnche" attributetype="Link" />
			<attribute name="CorpoTesto" attributetype="Hypertext" indexingtype="text" />
			<attribute name="Foto" attributetype="Image" />
			<attribute name="Data" attributetype="Date" searcheable="true" />
			<attribute name="Numero" attributetype="Number" searcheable="true" />
		</attributes>
	</contenttype>
	<contenttype typecode="EVN" typedescr="Evento" viewpage="contentview" listmodel="51" defaultmodel="5">
		<attributes>
			<attribute name="Titolo" attributetype="Text" searcheable="true" indexingtype="text" />
			<attribute name="CorpoTesto" attributetype="Hypertext" indexingtype="text" />
			<attribute name="DataInizio" attributetype="Date" searcheable="true" />
			<attribute name="DataFine" attributetype="Date" searcheable="true" />
			<attribute name="Foto" attributetype="Image" />
			<list name="LinkCorrelati" attributetype="Monolist">
				<nestedtype>
					<attribute name="LinkCorrelati" attributetype="Link" />
				</nestedtype>
			</list>
		</attributes>
	</contenttype>
	<contenttype typecode="RAH" typedescr="Tipo_Semplice" viewpage="contentview" listmodel="126" defaultmodel="457">
		<attributes>
			<attribute name="Titolo" attributetype="Text" indexingtype="text">
				<validations>
					<minlength>10</minlength>
					<maxlength>100</maxlength>
				</validations>
			</attribute>
			<attribute name="CorpoTesto" attributetype="Hypertext" indexingtype="text" />
			<attribute name="Foto" attributetype="Image" />
			<attribute name="email" attributetype="Monotext">
				<validations>
					<regexp><![CDATA[.+@.+.[a-z]+]]></regexp>
				</validations>
			</attribute>
			<attribute name="Numero" attributetype="Number" />
			<attribute name="Correlati" attributetype="Link" />
			<attribute name="Allegati" attributetype="Attach" />
			<attribute name="Checkbox" attributetype="CheckBox" />
		</attributes>
	</contenttype>
</contenttypes>

');
INSERT INTO `sysconfig_xxx` (version, item, descr, config) VALUES ('test', 'imageDimensions', 'Definition of the resized image dimensions', '<Dimensions>
	<Dimension>
		<id>1</id>
		<dimx>90</dimx>
		<dimy>90</dimy>
	</Dimension>
	<Dimension>
		<id>2</id>
		<dimx>130</dimx>
		<dimy>130</dimy>
	</Dimension>
	<Dimension>
		<id>3</id>
		<dimx>150</dimx>
		<dimy>150</dimy>
	</Dimension>
</Dimensions>
');
INSERT INTO `sysconfig_xxx` (version, item, descr, config) VALUES ('test', 'langs', 'Definition of the system languages', '<?xml version="1.0" encoding="UTF-8"?>
<Langs>
  <Lang>
    <code>it</code>
    <descr>Italiano</descr>
    <default>true</default>
  </Lang>
  <Lang>
    <code>en</code>
    <descr>English</descr>
  </Lang>
</Langs>

');
