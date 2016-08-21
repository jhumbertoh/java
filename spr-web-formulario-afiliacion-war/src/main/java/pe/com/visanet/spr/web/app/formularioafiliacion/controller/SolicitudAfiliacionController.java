package pe.com.visanet.spr.web.app.formularioafiliacion.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.com.visanet.spr.web.app.formularioafiliacion.util.Constantes;
import pe.com.visanet.spr.web.framework.controller.AbstractSolicitudAfiliacionController;
import pe.com.visanet.spr.web.framework.mvc.ActiveUser;
import pe.com.visanet.spr.web.framework.mvc.ClientIpAddress;
import pe.com.visanet.spr.web.logic.domain.Auditoria;
import pe.com.visanet.spr.web.logic.domain.CanalAfiliacion;
import pe.com.visanet.spr.web.logic.domain.Comercio;
import pe.com.visanet.spr.web.logic.domain.CondicionAfiliacion;
import pe.com.visanet.spr.web.logic.domain.DetalleAfiliacion;
import pe.com.visanet.spr.web.logic.domain.EstadoSolicitudAfiliacion;
import pe.com.visanet.spr.web.logic.domain.Modulo;
import pe.com.visanet.spr.web.logic.domain.Operacion;
import pe.com.visanet.spr.web.logic.domain.Producto;
import pe.com.visanet.spr.web.logic.domain.SolicitudAfiliacion;
import pe.com.visanet.spr.web.logic.domain.TipoDocumentoIdentidad;
import pe.com.visanet.spr.web.logic.domain.Usuario;
import pe.com.visanet.spr.web.logic.exception.LogicaException;
import pe.com.visanet.spr.web.logic.service.BinService;
import pe.com.visanet.spr.web.logic.service.ComercioService;
import pe.com.visanet.spr.web.logic.service.CondicionAfiliacionService;
import pe.com.visanet.spr.web.logic.service.GiroService;
import pe.com.visanet.spr.web.logic.service.OperacionService;
import pe.com.visanet.spr.web.logic.service.ProductoService;
import pe.com.visanet.spr.web.logic.service.SolicitudAfiliacionService;

import com.google.code.kaptcha.Constants;




/**
 * Controlador de solicitudes de afiliación
 */
@Controller
@SessionAttributes({"solicitudAfiliacionCmd"})

public class SolicitudAfiliacionController extends AbstractSolicitudAfiliacionController
{
    @Autowired
    private SolicitudAfiliacionService solicitudAfiliacionService;
    @Autowired
    private ComercioService comercioService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private GiroService giroService;
    @Autowired
    private BinService binService;
    @Autowired
    private CondicionAfiliacionService condicionAfiliacionService;
    @Autowired
    private OperacionService operacionService;
    
   
    
    
    private static final Logger logger = LoggerFactory.getLogger(SolicitudAfiliacionController.class);
    
    private static final String PAG_AFILIACION_INICIAR_CREAR="FormularioAfiliacion";
    private static final String PAG_SALIDA = "salida";
    
    /**
     * Maneja el acceso a la raiz de la aplicacion
     * 
     * @return vista lógica
     */
    @RequestMapping("/")
    public String manejarRaiz()
    {
        return "redirect:/formulario-afiliacion" ;
    }
    
    @RequestMapping("/salida")
    public String mostrarSalida()
    {
        return PAG_SALIDA;
    }
    
    /**
     * Inicia la pantalla de solicitud de afiliación
     */
    @RequestMapping(value="/formulario-afiliacion", method={RequestMethod.POST,RequestMethod.GET})
    public String iniciar(HttpServletRequest request, @RequestParam(value="codigoComercio", required=false) String codigoComercio, ModelMap modelo)
    {
        logger.debug("CODIGO DE COMERCIO EXTERNO: "+codigoComercio);
                
        if(codigoComercio!=null)
        {
            if(!codigoComercio.equals("")) 
            {
                SolicitudAfiliacion solicitudAfiliacion = new SolicitudAfiliacion();
                modelo.addAttribute("solicitudAfiliacionCmd", solicitudAfiliacion);
                
                Comercio comercio = comercioService.obtenerComercioCodigo(codigoComercio);
                List<Producto> listaProductos = productoService.buscarPorComercio(comercio.getId());
                
                modelo.addAttribute("listaProductos", listaProductos);
                modelo.addAttribute("comercio",comercio);
                
                DetalleAfiliacion detalleAfiliacion = new DetalleAfiliacion();
                Producto producto = new Producto();
                producto.setComercio(comercio);
                detalleAfiliacion.setMoneda(comercio.getMoneda());
                detalleAfiliacion.setProducto(producto);
                solicitudAfiliacion.setDetalleAfiliacion(detalleAfiliacion);
                
                llenarCombosFormularioExterno(modelo);
            }
        }
        else
        {
            SolicitudAfiliacion solicitudAfiliacion = new SolicitudAfiliacion();
            modelo.addAttribute("solicitudAfiliacionCmd", solicitudAfiliacion);
        }
        
        
        return PAG_AFILIACION_INICIAR_CREAR;
    }

    public void llenarCombosFormularioExterno(ModelMap modelo)
    {
        
        List<TipoDocumentoIdentidad> listaTipoDocumentoIdentidad = new ArrayList<TipoDocumentoIdentidad>(EnumSet.allOf(TipoDocumentoIdentidad.class));
        modelo.addAttribute("listaTipoDocumentoIdentidad", listaTipoDocumentoIdentidad);

        List<String> listaMeses = new ArrayList<String>();
        String valor = "";
        for(int i=1; i<= 12; i++)
        {
            if(i<10)
            {
                valor = "0" + i;
            }
            else
            {
                valor = i +"";
            }
            listaMeses.add(valor);
        }
        
        int anhoActual = Calendar.getInstance().get(Calendar.YEAR);
        
        List<Integer> listaAnhos = new ArrayList<Integer>();
        
        for(int i=anhoActual; i<= anhoActual + 10; i++)
        {
            listaAnhos.add(i);
        }
        
        modelo.addAttribute("listaMeses", listaMeses);
        modelo.addAttribute("listaAnhos", listaAnhos);
        
       
    }
    

    /**
     * Crea una solicitud de afiliación
     * 
     * @param solicitudAfiliacion
     *            Solicitud de afiliación a crear
     */
    @RequestMapping(value = "/formulario-afiliacion/crear", method= RequestMethod.POST)
    public String crear(@ModelAttribute("solicitudAfiliacionCmd") SolicitudAfiliacion solicitudAfiliacion, 
            BindingResult bindingResult,
            @ClientIpAddress String ipAddress,
            ModelMap modelo,
            final RedirectAttributes redirectAttributes,HttpServletRequest request)
    {
        Usuario usuarioActivo = new Usuario();
        usuarioActivo.setCodigo("ext_user");
        usuarioActivo.setModulo(Modulo.HOMEBANKING);
        usuarioActivo.setNombresCompletos("Usuario Externo");
        
        try
        {
            validateCaptcha(request, solicitudAfiliacion, bindingResult);
        }
        catch (Exception e1)
        {
            throw new RuntimeException(e1);
        }
        
        if(bindingResult.hasErrors())
        {
            List<Producto> listaProductos = productoService.buscarPorComercio(solicitudAfiliacion.getDetalleAfiliacion().getProducto().getComercio().getId());
            modelo.addAttribute("listaProductos", listaProductos);
            llenarCombosFormularioExterno(modelo);
            return PAG_AFILIACION_INICIAR_CREAR;
        }                     
        
        if(solicitudAfiliacion != null)
        {
            try{
                
                solicitudAfiliacion.setAuditoria(new Auditoria(usuarioActivo.getCodigo(),ipAddress));                
                solicitudAfiliacion.getDetalleAfiliacion().getTarjeta().setNumeroEnmascarado(solicitudAfiliacion.getDetalleAfiliacion().getTarjeta().getNumero());
                solicitudAfiliacion.getDetalleAfiliacion().setCanalAfiliacion(CanalAfiliacion.EXTERNO);
                solicitudAfiliacion.setEstadoSolicitudAfiliacion(EstadoSolicitudAfiliacion.PENDIENTE);
                                
                solicitudAfiliacionService.crear(solicitudAfiliacion, usuarioActivo);
                
                Operacion operacion = new Operacion();
                operacion.setFecha(solicitudAfiliacion.getAuditoria().getFechaCreacion());
                operacion.setTipoModulo(usuarioActivo.getModulo());
                operacion.setCodigoUsuario(usuarioActivo.getCodigo());
                operacion.setNombreCompletoUsuario(usuarioActivo.getNombresCompletos());
                operacion.setDescripcion("Creacion de Solicitud de Afiliacion (Formulario Externo)");
                operacion.setNumeroTarjetaEnmascaradoAfiliado(solicitudAfiliacion.getDetalleAfiliacion().getTarjeta().getNumeroEnmascarado());
                operacion.setTipoDocumentoIdentidad(solicitudAfiliacion.getDetalleAfiliacion().getTarjetahabiente().getTipoDocumentoIdentidad());
                operacion.setNumeroIdentidadAfiliado(solicitudAfiliacion.getDetalleAfiliacion().getTarjetahabiente().getNumeroDocumentoIdentidad());
                operacion.setNumeroIdentificacionBeneficiario(solicitudAfiliacion.getDetalleAfiliacion().getBeneficiario().getNumeroIdentificacion());
                operacionService.registrar(operacion, false);
                 
                
                
            }
            catch(LogicaException e){ 
                bindingResult.reject(e.getMessage());
                List<Producto> listaProductos = productoService.buscarPorComercio(solicitudAfiliacion.getDetalleAfiliacion().getProducto().getComercio().getId());
                modelo.addAttribute("listaProductos", listaProductos);
                llenarCombosFormularioExterno(modelo);
                return PAG_AFILIACION_INICIAR_CREAR;            
            }
        }
        
        modelo.clear();
        
        redirectAttributes.addFlashAttribute("outcome", "solicitudAfiliacion.registro.exitoso");
        redirectAttributes.addFlashAttribute("isRedirect", true);
        
        return "redirect:/" + PAG_SALIDA;
        
    }

    /**
     * Muestra los términos y condiciones de la afiliación
     */
    @RequestMapping(value="/condiciones-afiliacion/descargar",method=RequestMethod.GET)
    public String mostrarTerminosCondiciones(Model modelo)
    {
        CondicionAfiliacion condicionAfiliacion= condicionAfiliacionService.buscarPorId(Constantes.ID_CONDICION_AFILIACION);
        if(condicionAfiliacion==null){
            return "condicion de afiliacion inexistente ";
        }        
        modelo.addAttribute("objeto",condicionAfiliacion);
        
        return Constantes.DOC_EXPORTAR_PDF;    
    }

       
    /**
     * Verifica la existencia de un bin
     * 
     * @param numeroBin
     *            Número de bin a verificar
     */
    @RequestMapping("/formulario-afiliacion/verificarbin/{numeroTarjeta}")
    public @ResponseBody
    String verificarBin(@PathVariable String numeroTarjeta, ModelMap modelo, @ActiveUser Usuario usuarioActivo)
    {
        return super.verificarBin(numeroTarjeta, usuarioActivo);
    }
    
    /**
     * Validate the captcha response
     */
    protected void validateCaptcha(  HttpServletRequest request, SolicitudAfiliacion solicitudAfiliacion, BindingResult bindingResult) throws Exception 
    {
        String captchaId = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        String response = solicitudAfiliacion.getCaptchaResponse();
        
        logger.debug("captchaId : '" + captchaId + "'");
        logger.debug("Validating captcha response: '" + response + "'");

        if (!StringUtils.equalsIgnoreCase(captchaId, response)) 
        {
            bindingResult.rejectValue("captchaResponse", "solicitudAfiliacion.registration.captcha");
        }
    }
}
    

