/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agiletec.apsadmin.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.Unchainable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;


/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * An interceptor that copies all the properties of every object in the value stack to the currently executing object,
 * except for any object that implements {@link Unchainable}. A collection of optional <i>includes</i> and
 * <i>excludes</i> may be provided to control how and which parameters are copied. Only includes or excludes may be
 * specified. Specifying both results in undefined behavior. See the javadocs for {@link ReflectionProvider#copy(Object, Object,
 * java.util.Map, java.util.Collection, java.util.Collection)} for more information.
 * <p/>
 * <p/>
 * <b>Note:</b> It is important to remember that this interceptor does nothing if there are no objects already on the stack.
 * <br/>This means two things:
 * <br/><b>One</b>, you can safely apply it to all your actions without any worry of adverse affects.
 * <br/><b/>Two</b>, it is up to you to ensure an object exists in the stack prior to invoking this action. The most typical way this is done
 * is through the use of the <b>chain</b> result type, which combines with this interceptor to make up the action
 * chaining feature.
 * <p/>
 * <!-- END SNIPPET: description -->
 * <p/>
 * <p/> <u>Interceptor parameters:</u>
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <p/>
 * <ul>
 * <p/>
 * <li>excludes (optional) - the list of parameter names to exclude from copying (all others will be included).</li>
 * <p/>
 * <li>includes (optional) - the list of parameter names to include when copying (all others will be excluded).</li>
 * <p/>
 * <li>includeParameters (optional) - parameter names (comma delimited string) to include when copying (all others will be excluded).</li>
 * <p/>
 * <li>excludeParameters (optional) - parameter names (comma delimited string) to exclude from copying (all others will be included).</li>
 * <p/>
 * <li>compoundRootMinSize (optional) - min size of chain action classes to exclude or include parameters.</li>
 * <p/>
 * </ul>
 * <p/>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <p/> <u>Extending the interceptor:</u>
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * There are no known extension points to this interceptor.
 * <p/>
 * <!-- END SNIPPET: extending -->
 * <p/>
 * <p/> <u>Example code:</u>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example -->
 * <p/>
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success" type="chain"&gt;otherAction&lt;/result&gt;
 * &lt;/action&gt;
 * <p/>
 * &lt;action name="otherAction" class="com.examples.OtherAction"&gt;
 *     &lt;interceptor-ref name="chain"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <p/>
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author mrdon
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @author E.Santoboni
 * @see com.opensymphony.xwork2.ActionChainResult
 */
public class ChainingInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ChainingInterceptor.class);

    protected Collection<String> excludes;
    protected Collection<String> includes;
    private Integer compoundRootMinSize = 3;
    
	protected ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }
    
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ValueStack stack = invocation.getStack();
        CompoundRoot root = stack.getRoot();
        if (root.size() >= this.compoundRootMinSize && isChainResult(invocation)) {
            List<CompoundRoot> list = new ArrayList<CompoundRoot>(root);
            list.remove(0);
            Collections.reverse(list);
            Map<String, Object> ctxMap = invocation.getInvocationContext().getContextMap();
            Iterator<CompoundRoot> iterator = list.iterator();
            int index = 1; // starts with 1, 0 has been removed
            while (iterator.hasNext()) {
                index = index + 1;
                Object o = iterator.next();
                if (o != null) {
                    if (!(o instanceof Unchainable)) {
                        reflectionProvider.copy(o, invocation.getAction(), ctxMap, excludes, includes);
                    }
                } else {
                    LOG.warn("compound root element at index " + index + " is null");
                }
            }
        }
        return invocation.invoke();
    }
    
    private boolean isChainResult(ActionInvocation invocation) throws Exception {
        Result result = invocation.getResult();
        return (result != null && ActionChainResult.class.isAssignableFrom(result.getClass())) || 
        	(null != includes && includes.size() > 0) || (null != excludes && excludes.size() > 0);
    }
    
    /**
     * Gets list of parameter names to exclude
     * @return the exclude list
     */
    public Collection<String> getExcludes() {
        return excludes;
    }

    /**
     * Sets the list of parameter names to exclude from copying (all others will be included).
     * @param excludes the excludes list
     */
    public void setExcludes(Collection<String> excludes) {
        this.excludes = excludes;
    }

    /**
     * Gets list of parameter names to include
     * @return the include list
     */
    public Collection<String> getIncludes() {
        return includes;
    }

    /**
     * Sets the list of parameter names to include when copying (all others will be excluded).
     * @param includes the includes list
     */
    public void setIncludes(Collection<String> includes) {
        this.includes = includes;
    }
    
    public void setExcludeParameters(String parametersToExclude) {
    	this.excludes = TextParseUtil.commaDelimitedStringToSet(parametersToExclude);
	}
	
	public void setIncludeParameters(String parametersToInclude) {
		this.includes = TextParseUtil.commaDelimitedStringToSet(parametersToInclude);
	}
	
    public Integer getCompoundRootMinSize() {
		return compoundRootMinSize;
	}
	public void setCompoundRootMinSize(Integer compoundRootMinSize) {
		this.compoundRootMinSize = compoundRootMinSize;
	}
	
}