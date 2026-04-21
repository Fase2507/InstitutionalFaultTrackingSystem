package tr.duzce.edu.mf.bm.KurumArizaTakip.config;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import tr.duzce.edu.mf.bm.KurumArizaTakip.web.logging.RequestIdFilter;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // Veritabanı ve servis katmanı konfigürasyonları
         return new Class[] { PersistenceConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // Web (Controller, ViewResolver) konfigürasyonları
        return new Class[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        // Tüm istekleri Spring'e yönlendir
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        // Karakter kodlama filtresi
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        // İstek ID filtresi (Geçici olarak devre dışı)
        // return new Filter[] { encodingFilter, new RequestIdFilter() };
        return new Filter[] { encodingFilter };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        // MySQL temizleme listener'ını kaydet
        servletContext.addListener(new MysqlCleanupListener());
    }
}
