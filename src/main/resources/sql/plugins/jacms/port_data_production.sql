INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('content_viewer', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Contents - Publish a Content</property>
<property key="it">Contenuti - Pubblica un Contenuto</property>
</properties>', '<config>
	<parameter name="contentId">Content ID</parameter>
	<parameter name="modelId">Content Model ID</parameter>
	<action name="viewerConfig"/>
</config>', 'jacms', NULL, NULL, 1, NULL);
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('search_result', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Search - Search Result</property>
<property key="it">Ricerca - Risultati della Ricerca</property>
</properties>', NULL, 'jacms', NULL, NULL, 1, NULL);
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('content_viewer_list', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Contents - Publish a List of Contents</property>
<property key="it">Contenuti - Pubblica una Lista di Contenuti</property>
</properties>', '<config>
	<parameter name="contentType">Content Type (mandatory)</parameter>
	<parameter name="modelId">Content Model</parameter>
	<parameter name="userFilters">Front-End user filter options</parameter>
	<parameter name="category">Content Category **deprecated**</parameter>
	<parameter name="categories">Content Category codes (comma separeted)</parameter>
	<parameter name="orClauseCategoryFilter" />
	<parameter name="maxElemForItem">Contents for each page</parameter>
	<parameter name="maxElements">Number of contents</parameter>
	<parameter name="filters" />
	<parameter name="title_{lang}">Widget Title in lang {lang}</parameter>
	<parameter name="pageLink">The code of the Page to link</parameter>
	<parameter name="linkDescr_{lang}">Link description in lang {lang}</parameter>
	<action name="listViewerConfig"/>
</config>', 'jacms', NULL, NULL, 1, NULL);




INSERT INTO showletconfig (pagecode, framepos, showletcode, config, publishedcontent) VALUES ('homepage', 0, 'content_viewer', NULL, NULL);




INSERT INTO sysconfig (version, item, descr, config) VALUES ('production', 'contentTypes', 'Definition of the Content Types', '<contenttypes>
</contenttypes>');
INSERT INTO sysconfig (version, item, descr, config) VALUES ('production', 'imageDimensions', 'Definition of the resized image dimensions', '<Dimensions>
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
INSERT INTO sysconfig (version, item, descr, config) VALUES ('production', 'subIndexDir', 'Name of the sub-directory containing content indexing files', 'index');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('SEARCH', 'en', 'Search');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('SEARCH', 'it', 'Cerca');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('SEARCH_FILTERS_BUTTON', 'en', 'Narrow your search');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('SEARCH_FILTERS_BUTTON', 'it', 'Filtra ulteriormente');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('LIST_VIEWER_EMPTY', 'en', 'No result found. Broaden your search and try again!');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('LIST_VIEWER_EMPTY', 'it', 'Nessun risultato trovato. Imposta dei parametri meno stringenti e prova ancora!');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('jacms_LIST_VIEWER_FIELD', 'en', 'The value for field');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('jacms_LIST_VIEWER_FIELD', 'it', 'Il valore del campo');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('jacms_LIST_VIEWER_INVALID_FORMAT', 'en', 'is invalid');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('jacms_LIST_VIEWER_INVALID_FORMAT', 'it', 'non è corretto');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('EDIT_THIS_CONTENT', 'en', 'Edit');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('EDIT_THIS_CONTENT', 'it', 'Modifica');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_RESULTS', 'en', 'Search Result');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_RESULTS', 'it', 'Risultati della Ricerca');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCHED_FOR', 'en', 'You searched for');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCHED_FOR', 'it', 'Hai cercato');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_NOTHING_FOUND', 'en', 'No result found. Try another search term!');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_NOTHING_FOUND', 'it', 'Nessun risultato trovato. Prova a cercare un altro termine!');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_RESULTS_INTRO', 'en', 'Found');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_RESULTS_INTRO', 'it', 'Sono stati trovati');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_RESULTS_OUTRO', 'en', 'results. Showing:');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('SEARCH_RESULTS_OUTRO', 'it', 'risultati. Mostrati:');
