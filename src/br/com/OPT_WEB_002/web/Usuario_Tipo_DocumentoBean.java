package br.com.OPT_WEB_002.web;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.com.OPT_WEB_002.tipo_documento.Tipo_Documento;
import br.com.OPT_WEB_002.usuario.Usuario;
import br.com.OPT_WEB_002.usuario_tipo_documento.*;


@javax.faces.bean.ManagedBean(name = "usuario_tipo_docBean")
@SessionScoped
public class Usuario_Tipo_DocumentoBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigInteger id_usuario_tipo_documento;
	private Usuario_Tipo_Documento usuario_Tipo_Documento = new Usuario_Tipo_Documento();
	private Usuario_Tipo_DocumentoRN usuario_Tipo_DocumentoRN;
	private Usuario_Tipo_Documento usuario_Tipo_DocumentoSelecionado = new Usuario_Tipo_Documento();
	private Tipo_Documento tipo_Documento = new Tipo_Documento();
	

	public Usuario_Tipo_DocumentoBean() {}
	
	public Usuario_Tipo_Documento iniciar(Usuario usuario){
		
		if(id_usuario_tipo_documento == null){
		
			usuario_Tipo_Documento.getCod_empresa().setCod_empresa(usuario.getCod_empresa().getCod_empresa());
			usuario_Tipo_Documento.getCod_filial().setCod_filial(usuario.getCod_filial().getCod_filial());
			usuario_Tipo_Documento.getCod_unidade().setCod_unidade(usuario.getCod_unidade().getCod_unidade());
		    
			return usuario_Tipo_Documento;
			
		}else{
			
			Usuario_Tipo_DocumentoRN usuario_Tipo_DocumentoRN = new Usuario_Tipo_DocumentoRN();
			usuario_Tipo_Documento = usuario_Tipo_DocumentoRN.carregar(id_usuario_tipo_documento);			
			return usuario_Tipo_Documento;
		}	
		
	}
	
	/**public List<Usuario_Tipo_Documento> listarPorIdTipoDocumento(BigInteger id_tipo_doc,Integer cod_empresa,Integer cod_filial,Integer cod_unidade){
		
		usuario_Tipo_DocumentoRN = new Usuario_Tipo_DocumentoRN();
		return usuario_Tipo_DocumentoRN.listarPorIdTipoDocCodEmCodFiCodUni(id_tipo_doc, cod_empresa, cod_filial, cod_unidade);	
		
	}**/
	
	public List<Usuario_Tipo_Documento> listarPorCodEmCodFiCodUni(Usuario usuario){
		
		usuario_Tipo_DocumentoRN = new Usuario_Tipo_DocumentoRN();
				
		return usuario_Tipo_DocumentoRN.listarPorCodEmCodFiCodUni(usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade());	
	}
	
	public String novo(){
		
		return "/restrito/usuario_tipo_documento/cadastro_usuario_tipo_documento.xhtml?faces-redirect=true";
	}
	
	public String alterar(){
		
		return  "/restrito/usuario_tipo_documento/cadastro_usuario_tipo_documento.xhtml?id=" + usuario_Tipo_DocumentoSelecionado.getId_usuario_tipo_doc() + "faces-redirect=true";
	}
	
		
	public String excluir(){
			
		usuario_Tipo_DocumentoRN = new Usuario_Tipo_DocumentoRN();
		
		usuario_Tipo_Documento = usuario_Tipo_DocumentoRN.carregar(usuario_Tipo_DocumentoSelecionado.getId_usuario_tipo_doc());
		
		usuario_Tipo_DocumentoRN.excluir(usuario_Tipo_Documento);
		
		return "/restrito/usuario_tipo_documento/usuario_tipo_documento.xhtml?faces-redirect=true";
	}
	
	public String salvar(){
		
		usuario_Tipo_DocumentoRN = new Usuario_Tipo_DocumentoRN();
		
		if(id_usuario_tipo_documento == null){
									
			if(usuario_Tipo_DocumentoRN.carregarPorIdUsuIdTipo(usuario_Tipo_Documento.getId_usuario().getId_usuario(),usuario_Tipo_Documento.getId_tipo_doc().getId_tipo_doc()) != null){
				
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "Tipo Documento duplicado!"));
				return null;
				
			}else{
				
				usuario_Tipo_DocumentoRN.salvar(usuario_Tipo_Documento);				
				return "/restrito/usuario_tipo_documento/usuario_tipo_documento.xhtml?faces-redirect=true";
				
			}
						
		}else{
			System.out.println("alterar");
			usuario_Tipo_DocumentoRN.alterar(usuario_Tipo_Documento);			
			return "/restrito/usuario_tipo_documento/usuario_tipo_documento.xhtml?faces-redirect=true";
		}
		
	}
	
	public List<Usuario_Tipo_Documento> listarPorIdUsuarioCodEmCodFiCodUni(Usuario usuario) {
	
		 usuario_Tipo_DocumentoRN = new Usuario_Tipo_DocumentoRN();
		
		return usuario_Tipo_DocumentoRN.listarPorIdUsuarioCodEmCodFiCodUni(usuario.getId_usuario(),usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade());
		 		
	}

	public BigInteger getId_usuario_tipo_documento() {
		return id_usuario_tipo_documento;
	}

	public void setId_usuario_tipo_documento(BigInteger id_usuario_tipo_documento) {
		this.id_usuario_tipo_documento = id_usuario_tipo_documento;
	}

	public Usuario_Tipo_Documento getUsuario_Tipo_Documento() {
		return usuario_Tipo_Documento;
	}

	public void setUsuario_Tipo_Documento(Usuario_Tipo_Documento usuario_Tipo_Documento) {
		this.usuario_Tipo_Documento = usuario_Tipo_Documento;
	}

	public Usuario_Tipo_DocumentoRN getUsuario_Tipo_DocumentoRN() {
		return usuario_Tipo_DocumentoRN;
	}

	public void setUsuario_Tipo_DocumentoRN(Usuario_Tipo_DocumentoRN usuario_Tipo_DocumentoRN) {
		this.usuario_Tipo_DocumentoRN = usuario_Tipo_DocumentoRN;
	}

	public Usuario_Tipo_Documento getUsuario_Tipo_DocumentoSelecionado() {
		return usuario_Tipo_DocumentoSelecionado;
	}

	public void setUsuario_Tipo_DocumentoSelecionado(Usuario_Tipo_Documento usuario_Tipo_DocumentoSelecionado) {
		this.usuario_Tipo_DocumentoSelecionado = usuario_Tipo_DocumentoSelecionado;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id_usuario_tipo_documento == null) ? 0 : id_usuario_tipo_documento.hashCode());
		result = prime * result + ((usuario_Tipo_Documento == null) ? 0 : usuario_Tipo_Documento.hashCode());
		result = prime * result + ((usuario_Tipo_DocumentoRN == null) ? 0 : usuario_Tipo_DocumentoRN.hashCode());
		result = prime * result
				+ ((usuario_Tipo_DocumentoSelecionado == null) ? 0 : usuario_Tipo_DocumentoSelecionado.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario_Tipo_DocumentoBean other = (Usuario_Tipo_DocumentoBean) obj;
		if (id_usuario_tipo_documento == null) {
			if (other.id_usuario_tipo_documento != null)
				return false;
		} else if (!id_usuario_tipo_documento.equals(other.id_usuario_tipo_documento))
			return false;
		if (usuario_Tipo_Documento == null) {
			if (other.usuario_Tipo_Documento != null)
				return false;
		} else if (!usuario_Tipo_Documento.equals(other.usuario_Tipo_Documento))
			return false;
		if (usuario_Tipo_DocumentoRN == null) {
			if (other.usuario_Tipo_DocumentoRN != null)
				return false;
		} else if (!usuario_Tipo_DocumentoRN.equals(other.usuario_Tipo_DocumentoRN))
			return false;
		if (usuario_Tipo_DocumentoSelecionado == null) {
			if (other.usuario_Tipo_DocumentoSelecionado != null)
				return false;
		} else if (!usuario_Tipo_DocumentoSelecionado.equals(other.usuario_Tipo_DocumentoSelecionado))
			return false;
		return true;
	}

	public Tipo_Documento getTipo_Documento() {
		return tipo_Documento;
	}

	public void setTipo_Documento(Tipo_Documento tipo_Documento) {
		this.tipo_Documento = tipo_Documento;
	}

}
