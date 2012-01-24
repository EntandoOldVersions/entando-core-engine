/*
 *
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 * This file is part of jAPS software.
 * jAPS is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.aps.system;

/**
 * Interfaccia con le principali costanti di sistema.
 * @author 
 */
public interface SystemConstants {
    
    /**
     * Nome della property che definisce la versione 
     * di configurazione da utilizzare (tipo: String)
     */
    public static final String INIT_PROP_CONFIG_VERSION = "configVersion";
    
    /**
     * Nome del parametro di configurazione che rappresenta l'URL esterno
     * della web application.
     */
    public static final String PAR_APPL_BASE_URL = "applicationBaseURL";
    
    /**
     * Nome del parametro di configurazione che rappresenta l'URL base
     * per le risorse su file
     */
    public static final String PAR_RESOURCES_ROOT_URL = "resourceRootURL";
    
    /**
     * Nome del parametro di configurazione che rappresenta il percorso base
     * su disco per le risorse su file
     */
    public static final String PAR_RESOURCES_DISK_ROOT = "resourceDiskRootFolder";
    
    /**
     * Nome parametro extra per requestContext: lingua corrente
     */
    public static final String EXTRAPAR_CURRENT_LANG = "currentLang";
    
    /**
     * Nome parametro extra per requestContext: pagina corrente
     */
    public static final String EXTRAPAR_CURRENT_PAGE = "currentPage";
    
    /**
     * Nome parametro extra per requestContext: showlet corrente
     */
    public static final String EXTRAPAR_CURRENT_SHOWLET = "currentShowlet";
    /**
     * Nome parametro extra per requestContext: frame corrente
     */
    public static final String EXTRAPAR_CURRENT_FRAME = "currentFrame";
    
    /**
     * Nome parametro extra per requestContext: titoli extra pagina corrente
     */
    public static final String EXTRAPAR_EXTRA_PAGE_TITLES = "extraPageTitles";
    
    /**
     * Nome parametro extra per requestContext: external redirect
     */
    public static final String EXTRAPAR_EXTERNAL_REDIRECT = "externalRedirect";
    
    /**
     * Nome parametro extra per requestContext: Head Info Container
     */
    public static final String EXTRAPAR_HEAD_INFO_CONTAINER = "HeadInfoContainer";
    
    /**
     * Nome parametro di sessione: utente corrente
     */
    public static final String SESSIONPARAM_CURRENT_USER = "currentUser";
    
    /**
     * Nome del parametro di query string per l'identificatore di contenuto.
     */
    public static final String K_CONTENT_ID_PARAM = "contentId";
    
    /**
     * Nome del servizio che gestisce la configurazione del sistema.
     */
    public static final String BASE_CONFIG_MANAGER = "BaseConfigManager";
    
    /**
     * Nome del servizio che gestisce le lingue configurate nel sistema.
     */
    public static final String LANGUAGE_MANAGER = "LangManager";
    
    /**
     * Nome del servizio che gestisce i tipi di showlet.
     */
    public static final String SHOWLET_TYPE_MANAGER = "ShowletTypeManager";
    
    /**
     * Nome del servizio che gestisce i modelli di pagina.
     */
    public static final String PAGE_MODEL_MANAGER = "PageModelManager";
    
    
    /**
     * Nome del servizio che gestisce le pagine del portale.
     */
    public static final String PAGE_MANAGER = "PageManager";
    
    /**
     * Nome del servizio di gestione dei gruppi.
     */
    public static final String GROUP_MANAGER = "GroupManager";
    
    /**
     * Nome del servizio di gestione dei ruoli.
     */
    public static final String ROLE_MANAGER = "RoleManager";
    
    /**
     * Nome del del servizio di gestione degli utenti.
     */
    public static final String USER_MANAGER = "UserManager";
    
    /**
     * Nome del servizio di gestione degli URL.
     */
    public static final String URL_MANAGER = "URLManager";
    
    /**
     * Nome del servizio di gestione dell' i18n (localizzazione).
     */
    public static final String I18N_MANAGER = "I18nManager";
    
    /**
     * Nome del servizio che genera chiavi univoche (usate come id nelle tabelle) ad uso degli altri servizi.
     */
    public static final String KEY_GENERATOR_MANAGER = "KeyGeneratorManager";
    
    /**
     * Nome del servizio di gestione delle categorie.
     */
    public static final String CATEGORY_MANAGER = "CategoryManager";
    
    /**
     * Nome del servizio controller.
     */
    public static final String CONTROLLER_MANAGER = "ControllerManager";
    
    /**
     * Nome del servizio gestore cache.
     */
    public static final String CACHE_MANAGER = "CacheManager";
    
    public static final String AUTHENTICATION_PROVIDER_MANAGER = "AuthenticationProviderManager";
    
    public static final String AUTHORIZATION_SERVICE = "AuthorizationManager";
    
    public static final String API_RESPONSE_BUILDER = "ApiResponseBuilder";
    
    public static final String API_CATALOG_MANAGER = "ApiCatalogManager";
    
    public static final String API_LANG_CODE_PARAMETER = "apiMethod:langCode";
    
    public static final String API_USER_PARAMETER = "apiMethod:user";
    
    public static String[] API_RESERVED_PARAMETERS = {API_LANG_CODE_PARAMETER, API_USER_PARAMETER};
    
    public static final String OAUTH_CONSUMER_MANAGER = "OAuthConsumerManager";
    
    /**
     * Prefisso del nome del gruppo di oggetti in cache 
     * a servizio di una pagina.
     * Il nome và completato con il codice della pagina specifica.
     */
    public static final String PAGES_CACHE_GROUP_PREFIX = "PageCacheGroup_";
    
    /**
     * Formattazione di tutte le stringhe Date da utilizzare nel sistema.
     */
    public static final String SYSTEM_DATE_FORMAT = "yyyyMMdd";
    
    public static final String CONFIG_ITEM_LANGS = "langs";
    
    public static final String CONFIG_ITEM_PARAMS = "params";
    
    /**
     * Parametro di sistema: abilitazione del modulo Privacy.
     * Possibili immissioni "true" o "false" (default).
     */
    public static final String CONFIG_PARAM_PM_ENABLED = "extendedPrivacyModuleEnabled";
    
    /**
     * Parametro di sistema a uso del modulo Privacy. Numero massimo di mesi consentiti dal ultimo accesso.
     * Nel caso che il modulo privacy sia attivo e che una utenza abbia oltrepassato la soglia massima di inattività 
     * dell'utenza definita da questo parametro, l'utenza sarà dichiarata scaduta e in occasione del login tutte le autorizzazioni 
     * verranno disabilitate.
     */
    public static final String CONFIG_PARAM_PM_MM_LAST_ACCESS = "maxMonthsSinceLastAccess";
    
    /**
     * Parametro di sistema a uso del modulo Privacy. Numero massimo di mesi consentiti dal ultimo cambio password.
     * Nel caso che il modulo privacy sia attivo e che una utenza presenti la password invariata per un tempo oltre la soglia massima 
     * definita da questo parametro, in occasione del login tutte le autorizzazioni verranno disabilitate.
     */
    public static final String CONFIG_PARAM_PM_MM_LAST_PASSWORD_CHANGE = "maxMonthsSinceLastPasswordChange";
    
    /**
     * Parametro di sistema per la definizione dello stile della url dei link generati. 
     * Se settato a 'standard', la url generata avrà la forma "applicationBaseUrl/langCode/pageCode.wp".
     * Se settato a 'breadcrumbs', la url generata avrà la forma "applicationBaseUrl/pages/langCode/pagePath/" dove pagePath è la concatenazione 
     * dei codici pagina dalla pagina radice alla pagina oggetto del link.
     */
    public static final String CONFIG_PARAM_URL_STYLE = "urlStyle";
    
    public static final String CONFIG_PARAM_TREE_STYLE_PAGE = "treeStyle_page";
    
    public static final String CONFIG_PARAM_TREE_STYLE_CATEGORY = "treeStyle_category";
    
    public static final String TREE_STYLE_CLASSIC = "classic";
    
    public static final String TREE_STYLE_REQUEST = "request";
    
    public static final String TREE_STYLE_LEVEL = "level";
    
    public static final String CONFIG_PARAM_URL_STYLE_CLASSIC = "classic";
    
    public static final String CONFIG_PARAM_START_LANG_FROM_BROWSER = "startLangFromBrowser";
    
    public static final String CONFIG_PARAM_URL_STYLE_BREADCRUMBS = "breadcrumbs";
    
    public static final String CONFIG_PARAM_HOMEPAGE_PAGE_CODE = "homePageCode";
    
    public static final String CONFIG_PARAM_HYPERTEXT_EDITOR_CODE = "hypertextEditor";
    
    public static final String CONFIG_PARAM_NOT_FOUND_PAGE_CODE = "notFoundPageCode";
    
    public static final String CONFIG_PARAM_ERROR_PAGE_CODE = "errorPageCode";
    
    public static final String CONFIG_PARAM_LOGIN_PAGE_CODE = "loginPageCode";
    
    public static final String NAVIGATOR_PARSER = "NavigatorParser";
    
    /**
     * Lo username dell'utente amministratore, utente di default 
     * con diritti massimi nel sistema.
     */
    public static final String ADMIN_USER_NAME = "admin";
    
    /**
     * Lo username dell'utente guest, utente di default 
     * con diritti di accesso minimi ad elementi del sistema.
     */
    public static final String GUEST_USER_NAME = "guest";
    
}