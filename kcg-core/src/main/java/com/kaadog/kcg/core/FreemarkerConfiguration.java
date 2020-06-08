/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaadog.kcg.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kaadog.kcg.core.exception.FreemarkerConfigurationException;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Version;

public class FreemarkerConfiguration {

    private Configuration        configuration;
    private List<TemplateLoader> templateLoaders = new ArrayList<>();

    public FreemarkerConfiguration(){
        this(Configuration.VERSION_2_3_29);
    }

    public FreemarkerConfiguration(Version version){
        this.configuration = new Configuration(version);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 添加一个 {@link TemplateLoader}，会重新生成 {@link MultiTemplateLoader}，如果存在 {@link TemplateLoader} 不会进行添加操作
     */
    public FreemarkerConfiguration addTemplateLoader(TemplateLoader templateLoader) {

        templateLoaders.add(templateLoader);

        recreateTemplateLoader();
        return this;
    }

    /**
     * 重建模板加载器
     */
    private void recreateTemplateLoader() {

        configuration.clearTemplateCache();
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders.stream()
                .toArray(TemplateLoader[]::new));
        configuration.setTemplateLoader(multiTemplateLoader);
    }

    /**
     * 移除模板加载器
     */
    public FreemarkerConfiguration removeTemplateLoader(String templateSource) {
        templateLoaders.forEach(templateLoader -> {
            try {

                if (templateLoader instanceof StringTemplateLoader) {
                    ((StringTemplateLoader) templateLoader).removeTemplate(templateSource);
                } else {
                    templateLoader.closeTemplateSource(templateSource);
                }

            } catch (IOException e) {
                throw new FreemarkerConfigurationException("无法正确的关闭模板数据源， templateSource：" + templateSource, e);
            }
        });

        recreateTemplateLoader();
        return this;
    }

    public List<TemplateLoader> getTemplateLoaders() {
        return templateLoaders;
    }

}
