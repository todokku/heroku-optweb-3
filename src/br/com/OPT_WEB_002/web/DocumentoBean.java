package br.com.OPT_WEB_002.web;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import br.com.OPT_WEB_002.NodeDocumento.NodeDocumento;
import br.com.OPT_WEB_002.documento.*;
import br.com.OPT_WEB_002.grupo_valores_possiveis_doc.*;
import br.com.OPT_WEB_002.layout_empresa.*;
import br.com.OPT_WEB_002.tipo_documento.*;
import br.com.OPT_WEB_002.transacao_documento.*;
import br.com.OPT_WEB_002.usuario.*;
import br.com.OPT_WEB_002.usuario_tipo_documento.Usuario_Tipo_Documento;
import br.com.OPT_WEB_002.usuario_tipo_documento.Usuario_Tipo_DocumentoRN;
import br.com.OPT_WEB_002.util.*;
import br.com.OPT_WEB_002.val_campos_doc.*;
import br.com.OPT_WEB_002.val_campos_trans_doc.*;
import net.sf.jasperreports.engine.JRException;

@ManagedBean(name = "documentoBean")
@ViewScoped
public class DocumentoBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@ManagedProperty("#{nodeDocumentoBean}")	
	private NodeDocumento nodeDocumento;
	private Usuario usuario;
	private Documento documento = new Documento();	
	private Documento doc = new Documento();	
	private Documento documentoSelecionado; 
	private TreeNode nodePrincipal;	
	private TreeNode nodeselecionado;
	private BigInteger id;
	private BigInteger idDocSelecionado;
	private BigInteger id_doc;
	private BigInteger id_trans_doc;
	private Integer id_val_camp_trans_doc;
	private BigInteger id_tipo_doc;
	private LazyDataModel<Documento> lazymodel;
	private UploadedFile arquivoDoc;
	private StreamedContent streamedContent;
	private StreamedContent streamedContentDoc;
	/**variavel para carregar lista lazy documento**/
	private Tipo_Documento tipo_Documento = new Tipo_Documento();	
	private boolean linhaSelecionada = false;
	private BigInteger idDocDetalhe = null;
	private List<modeloColuna> columns;
	private boolean desabilitaCampoIncremento = false;	
					
	public DocumentoBean() {}
		   	   	
	/**retorna o usuario que esta utilizando o sistema**/
	public Usuario retornaLogin(Usuario usuario) {		 
		this.usuario = usuario;
		return this.usuario;
	 }

	public void recebeIdtipoDocLista(String id_tipo) {
		
		id_tipo_doc = BigInteger.valueOf(Long.valueOf(id_tipo));
	}
	
	
	/**Lista objetos com carregamento lazy**/
	public LazyDataModel<Documento> lazyDocumento(Usuario usuario) {
     
		if(id_tipo_doc != null) {
		/**objeto da classe LazyDocumento recebe uma lista de valores pelo campo id_tipo_doc**/
		lazymodel = new LazyDocumento(listarPorIdTipoDocCodEmpCodFiCodUni(usuario));	
		
		criarColunasDinamicas();
		  
		/**objeto com valor adicionado para true para carregar dados da datatable de documento e de transacao documento**/
		linhaSelecionada = true;
		
		return lazymodel;
		}
		/**método que cria colunas a partir de sua ordem cadastrada na tabela layout_empresa**/
       	
	return null;
	}
	
	/**Lista objetos com carregamento lazy**/
	public LazyDataModel<Documento> lazyDocumentoQrCode(Usuario usuario) {
		
		/**objeto da classe LazyDocumento recebe uma lista de valores pelo campo id_tipo_doc**/
		lazymodel = new LazyDocumento(listarPorIdTipoDocCodEmpCodFiCodUni(usuario));	
		
		/**método que cria colunas a partir de sua ordem cadastrada na tabela layout_empresa**/
		criarColunasDinamicasPorQrCode();
				
	return lazymodel;
	
	}
	
	/**método que verifica quais os campos devem ser mostrados a partir da marcado**/
	public boolean comparaFlagCampo(BigInteger id_tipo_doc,String cod_campo,Usuario usuario,Integer id){
		
		Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN(); 
					
			try{
				
				if(layout_EmpresaRN.listarPorIdTipoDocCodCampo(id_tipo_doc,cod_campo,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade()).getSequencia().equals(id)){
				     				
					return true;
					
				}else{
					
					return false;
				}
				
			}catch(NullPointerException e){
					
					return false;				
			}
		
	}
	
	
	/**método que verifica quais os campos devem ser mostrados a partir da marcado**/
	public boolean comparaQrCode(BigInteger id_tipo_doc,String cod_campo,Usuario usuario,Integer id){
		
		Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN(); 	
					
			try{
				
				if(layout_EmpresaRN.listarPorIdTipoDocCodCampo(id_tipo_doc,cod_campo,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade()).getSequencia().equals(id)){
				     								
					return true;
					
				}else{
					
					return false;
				}
				
			}catch(Exception e){
				
				e.printStackTrace();
				return false;				
			}
		
	}
	
	/**método que cria colunas dinamicas a partir dos campos relacionados entre layout x tipo_documento**/
	public List<modeloColuna> criarColunasDinamicasPorQrCode() {
					
		List<Integer> listaSeq = new ArrayList<Integer>();
		DocumentoRN documentoRN = new DocumentoRN();
					   
	   if(linhaSelecionada == false){ 	
			
			columns = new ArrayList<modeloColuna>();
		
	    	Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN();
	    	
	    	/**objeto do tipo modelocoluna com setter para o atributo header**/
	    	modeloColuna coluna = new modeloColuna(null, null); 
	    	coluna.setHeader(descricaoColunas("char_001", usuario));
					
			modeloColuna coluna2 = new modeloColuna(null,null);	        		   	
		   	coluna2.setHeader(descricaoColunas("char_002", usuario));
	    		   	   	
		   	modeloColuna coluna3  = new modeloColuna(null,null);;        		   	
		   	coluna3.setHeader(descricaoColunas("char_003", usuario));
		   	
		   	modeloColuna coluna4  = new modeloColuna(null,null);;	        		   	
		   	coluna4.setHeader(descricaoColunas("char_004", usuario));
		   	
		   	modeloColuna coluna5  = new modeloColuna(null,null);;	        		   	
		   	coluna5.setHeader(descricaoColunas("char_005", usuario));
		   	
		   	modeloColuna coluna6  = new modeloColuna(null,null);;	        		   	
		   	coluna6.setHeader(descricaoColunas("char_006", usuario));
		   	
		   	modeloColuna coluna7  = new modeloColuna(null,null);;	        		   	
		   	coluna7.setHeader(descricaoColunas("char_007", usuario)); 
		   	
		   	modeloColuna coluna8  = new modeloColuna(null,null);;	        		   	
		   	coluna8.setHeader(descricaoColunas("char_008", usuario));
		   	
		   	modeloColuna coluna9  = new modeloColuna(null,null);;	        		   	
		   	coluna9.setHeader(descricaoColunas("char_009", usuario));
		   	
		   	modeloColuna coluna10  = new modeloColuna(null,null);;	        		   	
		   	coluna10.setHeader(descricaoColunas("char_010", usuario));
		   	
		   	modeloColuna coluna11 = new modeloColuna(null, null); 
	    	coluna11.setHeader(descricaoColunas("char_011", usuario));
					
	    	modeloColuna coluna12 = new modeloColuna(null,null);	        		   	
		   	coluna12.setHeader(descricaoColunas("char_012", usuario));
			   	   	
		   	modeloColuna coluna13  = new modeloColuna(null,null);;        		   	
		   	coluna13.setHeader(descricaoColunas("char_013", usuario));
		   	
		   	modeloColuna coluna14  = new modeloColuna(null,null);;	        		   	
		   	coluna14.setHeader(descricaoColunas("char_014", usuario));
		   	
		   	modeloColuna coluna15  = new modeloColuna(null,null);;	        		   	
		   	coluna15.setHeader(descricaoColunas("char_015", usuario));
		   	
		   	modeloColuna coluna16  = new modeloColuna(null,null);;	        		   	
		   	coluna16.setHeader(descricaoColunas("char_016", usuario));
		   	
		   	modeloColuna coluna17  = new modeloColuna(null,null);;	        		   	
		   	coluna17.setHeader(descricaoColunas("char_017", usuario)); 
		   	
		   	modeloColuna coluna18  = new modeloColuna(null,null);;	        		   	
		   	coluna18.setHeader(descricaoColunas("char_018", usuario));
		   	
		   	modeloColuna coluna19  = new modeloColuna(null,null);;	        		   	
		   	coluna19.setHeader(descricaoColunas("char_019", usuario));
		   	
		   	modeloColuna coluna20  = new modeloColuna(null,null);;	        		   	
		   	coluna20.setHeader(descricaoColunas("char_020", usuario));
		   	
		   	modeloColuna coluna21 = new modeloColuna(null, null); 
	    	coluna21.setHeader(descricaoColunas("int_001", usuario));
					
	    	modeloColuna coluna22 = new modeloColuna(null,null);	        		   	
		   	coluna22.setHeader(descricaoColunas("int_002", usuario));
			   	   	
		   	modeloColuna coluna23  = new modeloColuna(null,null);;        		   	
		   	coluna23.setHeader(descricaoColunas("int_003", usuario));
		   	
		   	modeloColuna coluna24  = new modeloColuna(null,null);;	        		   	
		   	coluna24.setHeader(descricaoColunas("int_004", usuario));
		   	
		   	modeloColuna coluna25  = new modeloColuna(null,null);;	        		   	
		   	coluna25.setHeader(descricaoColunas("int_005", usuario));
		   	
		   	modeloColuna coluna26  = new modeloColuna(null,null);;	        		   	
		   	coluna26.setHeader(descricaoColunas("int_006", usuario));
		   	
			modeloColuna coluna27  = new modeloColuna(null,null);;	        		   	
		   	coluna27.setHeader(descricaoColunas("int_007", usuario)); 
		   	
			modeloColuna coluna28  = new modeloColuna(null,null);;	        		   	
		   	coluna28.setHeader(descricaoColunas("int_008", usuario));
		   	
		   	modeloColuna coluna29  = new modeloColuna(null,null);;	        		   	
		   	coluna29.setHeader(descricaoColunas("int_009", usuario));
		   	
			modeloColuna coluna30  = new modeloColuna(null,null);;	        		   	
		   	coluna30.setHeader(descricaoColunas("int_010", usuario));
		   	
	    	modeloColuna coluna31 = new modeloColuna(null, null); 
	    	coluna31.setHeader(descricaoColunas("dec_001", usuario));
					
			modeloColuna coluna32 = new modeloColuna(null,null);	        		   	
		   	coluna32.setHeader(descricaoColunas("dec_002", usuario));
			   	   	
		   	modeloColuna coluna33  = new modeloColuna(null,null);;        		   	
		   	coluna33.setHeader(descricaoColunas("dec_003", usuario));
		   	
			modeloColuna coluna34  = new modeloColuna(null,null);;	        		   	
		   	coluna34.setHeader(descricaoColunas("dec_004", usuario));
		   	
		   	modeloColuna coluna35  = new modeloColuna(null,null);;	        		   	
		   	coluna35.setHeader(descricaoColunas("dec_005", usuario));
		   	
			modeloColuna coluna36  = new modeloColuna(null,null);;	        		   	
		   	coluna36.setHeader(descricaoColunas("dec_006", usuario));
		   	
			modeloColuna coluna37  = new modeloColuna(null,null);;	        		   	
		   	coluna37.setHeader(descricaoColunas("dec_007", usuario)); 
		   	
			modeloColuna coluna38  = new modeloColuna(null,null);;	        		   	
		   	coluna38.setHeader(descricaoColunas("dec_008", usuario));
		   	
		   	modeloColuna coluna39  = new modeloColuna(null,null);;	        		   	
		   	coluna39.setHeader(descricaoColunas("dec_009", usuario));
		   	
			modeloColuna coluna40  = new modeloColuna(null,null);;	        		   	
		   	coluna40.setHeader(descricaoColunas("dec_010", usuario));
		   	
			modeloColuna coluna41  = new modeloColuna(null,null);;	        		   	
		   	coluna41.setHeader(descricaoColunas("data_001", usuario));
		   	
	    	modeloColuna coluna42 = new modeloColuna(null, null); 
	    	coluna42.setHeader(descricaoColunas("data_002", usuario));
					
			modeloColuna coluna43 = new modeloColuna(null,null);	        		   	
		   	coluna43.setHeader(descricaoColunas("data_003", usuario));
			   	   	
		   	modeloColuna coluna44  = new modeloColuna(null,null);;        		   	
		   	coluna44.setHeader(descricaoColunas("data_004", usuario));
		   	
			modeloColuna coluna45  = new modeloColuna(null,null);;	        		   	
		   	coluna45.setHeader(descricaoColunas("data_005", usuario));
		   	
		   	modeloColuna coluna46  = new modeloColuna(null,null);;	        		   	
		   	coluna46.setHeader(descricaoColunas("data_006", usuario));
		   	
			modeloColuna coluna47  = new modeloColuna(null,null);;	        		   	
		   	coluna47.setHeader(descricaoColunas("data_007", usuario));
		   	
			modeloColuna coluna48  = new modeloColuna(null,null);;	        		   	
		   	coluna48.setHeader(descricaoColunas("data_008", usuario)); 
		   	
			modeloColuna coluna49  = new modeloColuna(null,null);;	        		   	
		   	coluna49.setHeader(descricaoColunas("data_009", usuario));
		   	
		    modeloColuna coluna50  = new modeloColuna(null,null);;	        		   	
		   	coluna50.setHeader(descricaoColunas("data_010", usuario));
		  
		   	/**condição para carregar colunas dinamicas**/  	
		   	if(id_tipo_doc != null){
    		     		    		 
	    		 /**bloco para listar todos os objetos da tabela layout que estejam com o campo flag com valor true**/
	    		 for(Layout_Empresa layout_Empresa : layout_EmpresaRN.listarCamposFlag(id_tipo_doc,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade())){
	    			 /**objeto array para adicionar todos os campos sequencia do bloco **/	    		
	    			 listaSeq.add(layout_Empresa.getSequencia());    			 
	    		 }	   			
	    		
	    		 for(Integer id : listaSeq){
	    		    		    			
	    			 try{    			
		    				 
							if(comparaFlagCampo(id_tipo_doc,"char_001",usuario,id)){	  
								
								coluna.setProperty("char_001");    
								columns.add(coluna);	    	        	   
							} 
						 	 
							if(comparaFlagCampo(id_tipo_doc,"char_002",usuario,id)){	  
								
								coluna2.setProperty("char_002");    	  
							    columns.add(coluna2);	
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_003",usuario,id)){
							 	
								coluna3.setProperty("char_003"); 
							 	columns.add(coluna3);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_004",usuario,id)){	  
								 
								coluna4.setProperty("char_004"); 		
								columns.add(coluna4);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_005",usuario,id)){	  
							 	
								coluna5.setProperty("char_005"); 
							 	columns.add(coluna5);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_006",usuario,id)){	
								
							 	coluna6.setProperty("char_006"); 
							 	columns.add(coluna6);
							} 
							    	
							if(comparaFlagCampo(id_tipo_doc,"char_007",usuario,id)){	  
								 
								coluna7.setProperty("char_007"); 		
								columns.add(coluna7);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_008", usuario, id)){	  
							 	
								coluna8.setProperty("char_008"); 
							 	columns.add(coluna8);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_009", usuario, id)){	  
							 	
								coluna9.setProperty("char_009"); 
							 	columns.add(coluna9);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_010",usuario, id)){
								
							 	coluna10.setProperty("char_010"); 
							 	columns.add(coluna10);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_011", usuario, id)){	  
								 
								coluna11.setProperty("char_011"); 		
								columns.add(coluna11);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_012", usuario, id)){
							 	
								coluna12.setProperty("char_012"); 
							 	columns.add(coluna12);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_013", usuario, id)){
								
							 	coluna13.setProperty("char_013"); 
							 	columns.add(coluna13);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"char_014", usuario, id)){	  
								 
								coluna14.setProperty("char_014"); 		
								columns.add(coluna14);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_015", usuario, id)){	  
							 	
								coluna15.setProperty("char_015"); 
							 	columns.add(coluna15);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_016", usuario, id)){	  
							 	
								coluna16.setProperty("char_016"); 
							 	columns.add(coluna16);
							} 
						
							
							if(comparaFlagCampo(id_tipo_doc,"char_017", usuario, id)){	  
								 
								coluna17.setProperty("char_017"); 		
							    columns.add(coluna17);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"char_018", usuario, id)){	  
							 	
								coluna18.setProperty("char_018"); 
							 	columns.add(coluna18);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"char_019", usuario, id)){	 
								
							 	coluna19.setProperty("char_019"); 
							 	columns.add(coluna19);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"char_020", usuario, id)){	 
								
							 	coluna20.setProperty("char_020"); 
							 	columns.add(coluna20);
							} 
						
						
							if(comparaFlagCampo(id_tipo_doc,"int_001", usuario, id)){	  
								
								coluna21.setProperty("int_001"); 		
								columns.add(coluna21);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"int_002", usuario, id)){
							 	
								coluna12.setProperty("int_002"); 
							 	columns.add(coluna22);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"int_003", usuario, id)){
								
							 	coluna21.setProperty("int_003"); 
							 	columns.add(coluna23);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"int_004", usuario, id)){
								 
								coluna24.setProperty("int_004"); 		
								columns.add(coluna24);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"int_005", usuario, id)){
							 	
								coluna25.setProperty("int_005"); 
							 	columns.add(coluna25);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"int_006", usuario, id)){
								
							 	coluna26.setProperty("int_006"); 
							 	columns.add(coluna26);
							} 
						
							
							if(comparaFlagCampo(id_tipo_doc,"int_007", usuario, id)){
								 
								coluna27.setProperty("int_007"); 		
								columns.add(coluna27);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"int_008", usuario, id)){
							 	
								coluna28.setProperty("int_008"); 
							 	columns.add(coluna28);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"int_009", usuario, id)){
								
							 	coluna29.setProperty("int_009"); 
							 	columns.add(coluna29);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"int_010", usuario, id)){	 
								
							 	coluna30.setProperty("int_010"); 
							 	columns.add(coluna30);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"dec_001", usuario, id)){
								 
								coluna31.setProperty("dec_001"); 		
								columns.add(coluna31);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"dec_002", usuario, id)){	  
							 	
								coluna32.setProperty("dec_002"); 
							 	columns.add(coluna32);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"dec_003", usuario, id)){	
								
							 	coluna33.setProperty("dec_003"); 
							 	columns.add(coluna33);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"dec_004", usuario, id)){	  
								 
								coluna34.setProperty("dec_004"); 		
								columns.add(coluna34);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"dec_005", usuario, id)){
							 	
								coluna35.setProperty("dec_005"); 
							 	columns.add(coluna35);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"dec_006", usuario, id)){	
								
							 	coluna36.setProperty("dec_006"); 
							 	columns.add(coluna36);
							} 
						
							
							if(comparaFlagCampo(id_tipo_doc,"dec_007", usuario, id)){	  
								 
								coluna37.setProperty("dec_007"); 		
								columns.add(coluna37);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"dec_008", usuario, id)){	  
							 	
								coluna38.setProperty("dec_008"); 
							 	columns.add(coluna38);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"dec_009", usuario, id)){
								
							 	coluna39.setProperty("dec_009"); 
							 	columns.add(coluna39);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"int_010", usuario, id)){
							 	coluna40.setProperty("dec_010"); 
							 	columns.add(coluna40);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"data_001", usuario, id)){	  
								 
								coluna41.setProperty("data_001"); 		
								columns.add(coluna41);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"data_002", usuario, id)){	  
							 	
								coluna42.setProperty("data_002"); 
							 	columns.add(coluna42);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"data_003", usuario, id)){
								
							 	coluna43.setProperty("data_003"); 
							 	columns.add(coluna43);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"data_004", usuario, id)){
								 
								coluna44.setProperty("data_004"); 		
								columns.add(coluna44);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"data_005", usuario, id)){	  
							 	
								coluna45.setProperty("data_005"); 
							 	columns.add(coluna45);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"data_006", usuario, id)){	
								
							 	coluna46.setProperty("data_006"); 
							 	columns.add(coluna46);
							} 
						
							
							if(comparaFlagCampo(id_tipo_doc,"data_007", usuario, id)){	  
								 
								coluna47.setProperty("data_007"); 		
								columns.add(coluna47);
							} 
						
							if(comparaFlagCampo(id_tipo_doc,"data_008", usuario, id)){	  
							 	
								coluna48.setProperty("data_008"); 
							 	columns.add(coluna48);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"data_009", usuario, id)){
								
							 	coluna49.setProperty("data_009"); 
							 	columns.add(coluna49);
							} 
							
							if(comparaFlagCampo(id_tipo_doc,"data_010", usuario, id)){
								
							 	coluna50.setProperty("data_010"); 
							 	columns.add(coluna50);
							} 
								    			 
    			 }catch(NullPointerException e){
    				 
    				 return null;
    			 }   	
 		    	 		    	
    		}
	    		 }	
		   	
		   	try{
		   		
			   	if(documentoRN.listar().getId_doc() != null){
		    		/**objeto com valor adicionado para true para carregar dados da datatable de documento e de transacao documento**/
		    		linhaSelecionada = true;
			   	}
		   	}catch(Exception e){
		   		e.printStackTrace();
		   		
		   	}
        		return columns;
         }
	  
		return null;
    }

	
	public BigInteger carregarDocTransDocCampAdic(Usuario usuario) {
				
		DocumentoRN documentoRN = new DocumentoRN();
		
		/**variavel recebe o valor false para nao carregar colunas dinamicas**/
		linhaSelecionada = false;
		
		try {
			
			if (id_tipo_doc != null) {
				
				this.id_doc = documentoRN.listarPorIdTipoDocCodEmpCodFiCodUni(id_tipo_doc,
																			  usuario.getCod_empresa().getCod_empresa(),
																			  usuario.getCod_filial().getCod_filial(),
																			  usuario.getCod_unidade().getCod_unidade()).get(0).getId_doc();
                
				return this.id_tipo_doc;

			} else {
							
				this.id_doc = null;
				id_trans_doc = null;
				this.documento = new Documento();
				this.documentoSelecionado = new Documento();
				
				return null;
			}

		} catch (Exception e) {
			
			this.id_doc = null;
			id_trans_doc = null;
			e.printStackTrace();
			return null;
		}

	}
	
	public class camposLayout implements Serializable{
		
		/**
		 * 
		 */
		
		private static final long serialVersionUID = 1L;

		private String descricao;
		
		private String cod_campo;
				
		public camposLayout(String cod_campo,String descricao){
			
			this.cod_campo = cod_campo;
			this.descricao = descricao;
			
		}
		
		
			public String getCod_campo() {			
				return cod_campo;
			}
			
			public void setCod_campo(String cod_campo) {
				this.cod_campo = cod_campo;
			}
			

			public String getDescricao() {			
				return descricao;
			}
			
			public void setDescricao(String descricao) {
				this.descricao = descricao;
			}
		
	}
	
	
	public class modeloColuna implements Serializable {
		

		private static final long serialVersionUID = 1L;

		/**variavel para receber valor para a coluna**/
		private String property	;	
		
		/**variavel para receber titulo para a coluna**/
		private String header;
					
		public modeloColuna(String property,String header) {
						
			this.header = header;
			this.property = property;	
		
		}
							
		public String getProperty() {
	
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}
		
	
		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}
		
	}

	/**método que cria colunas dinamicas a partir dos campos relacionados entre layout x tipo_documento**/
	public List<modeloColuna> criarColunasDinamicas() {
					
	List<Integer> listaId = new ArrayList<Integer>();
		
	   if(linhaSelecionada == false){ 	
			
			columns = new ArrayList<modeloColuna>();
		
	    	Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN();
	    	
	    	/**objeto do tipo modelocoluna com setter para o atributo header**/
	    	modeloColuna coluna = new modeloColuna(null, null); 
	    	coluna.setHeader(descricaoColunas("char_001", usuario));
					
			modeloColuna coluna2 = new modeloColuna(null,null);	        		   	
		   	coluna2.setHeader(descricaoColunas("char_002", usuario));
	    		   	   	
		   	modeloColuna coluna3  = new modeloColuna(null,null);;        		   	
		   	coluna3.setHeader(descricaoColunas("char_003", usuario));
		   	
		   	modeloColuna coluna4  = new modeloColuna(null,null);;	        		   	
		   	coluna4.setHeader(descricaoColunas("char_004", usuario));
		   	
		   	modeloColuna coluna5  = new modeloColuna(null,null);;	        		   	
		   	coluna5.setHeader(descricaoColunas("char_005", usuario));
		   	
		   	modeloColuna coluna6  = new modeloColuna(null,null);;	        		   	
		   	coluna6.setHeader(descricaoColunas("char_006", usuario));
		   	
		   	modeloColuna coluna7  = new modeloColuna(null,null);;	        		   	
		   	coluna7.setHeader(descricaoColunas("char_007", usuario)); 
		   	
		   	modeloColuna coluna8  = new modeloColuna(null,null);;	        		   	
		   	coluna8.setHeader(descricaoColunas("char_008", usuario));
		   	
		   	modeloColuna coluna9  = new modeloColuna(null,null);;	        		   	
		   	coluna9.setHeader(descricaoColunas("char_009", usuario));
		   	
		   	modeloColuna coluna10  = new modeloColuna(null,null);;	        		   	
		   	coluna10.setHeader(descricaoColunas("char_010", usuario));
		   	
		   	modeloColuna coluna11 = new modeloColuna(null, null); 
	    	coluna11.setHeader(descricaoColunas("char_011", usuario));
					
	    	modeloColuna coluna12 = new modeloColuna(null,null);	        		   	
		   	coluna12.setHeader(descricaoColunas("char_012", usuario));
			   	   	
		   	modeloColuna coluna13  = new modeloColuna(null,null);;        		   	
		   	coluna13.setHeader(descricaoColunas("char_013", usuario));
		   	
		   	modeloColuna coluna14  = new modeloColuna(null,null);;	        		   	
		   	coluna14.setHeader(descricaoColunas("char_014", usuario));
		   	
		   	modeloColuna coluna15  = new modeloColuna(null,null);;	        		   	
		   	coluna15.setHeader(descricaoColunas("char_015", usuario));
		   	
		   	modeloColuna coluna16  = new modeloColuna(null,null);;	        		   	
		   	coluna16.setHeader(descricaoColunas("char_016", usuario));
		   	
		   	modeloColuna coluna17  = new modeloColuna(null,null);;	        		   	
		   	coluna17.setHeader(descricaoColunas("char_017", usuario)); 
		   	
		   	modeloColuna coluna18  = new modeloColuna(null,null);;	        		   	
		   	coluna18.setHeader(descricaoColunas("char_018", usuario));
		   	
		   	modeloColuna coluna19  = new modeloColuna(null,null);;	        		   	
		   	coluna19.setHeader(descricaoColunas("char_019", usuario));
		   	
		   	modeloColuna coluna20  = new modeloColuna(null,null);;	        		   	
		   	coluna20.setHeader(descricaoColunas("char_020", usuario));
		   	
		   	modeloColuna coluna21 = new modeloColuna(null, null); 
	    	coluna21.setHeader(descricaoColunas("int_001", usuario));
					
	    	modeloColuna coluna22 = new modeloColuna(null,null);	        		   	
		   	coluna22.setHeader(descricaoColunas("int_002", usuario));
			   	   	
		   	modeloColuna coluna23  = new modeloColuna(null,null);;        		   	
		   	coluna23.setHeader(descricaoColunas("int_003", usuario));
		   	
		   	modeloColuna coluna24  = new modeloColuna(null,null);;	        		   	
		   	coluna24.setHeader(descricaoColunas("int_004", usuario));
		   	
		   	modeloColuna coluna25  = new modeloColuna(null,null);;	        		   	
		   	coluna25.setHeader(descricaoColunas("int_005", usuario));
		   	
		   	modeloColuna coluna26  = new modeloColuna(null,null);;	        		   	
		   	coluna26.setHeader(descricaoColunas("int_006", usuario));
		   	
			modeloColuna coluna27  = new modeloColuna(null,null);;	        		   	
		   	coluna27.setHeader(descricaoColunas("int_007", usuario)); 
		   	
			modeloColuna coluna28  = new modeloColuna(null,null);;	        		   	
		   	coluna28.setHeader(descricaoColunas("int_008", usuario));
		   	
		   	modeloColuna coluna29  = new modeloColuna(null,null);;	        		   	
		   	coluna29.setHeader(descricaoColunas("int_009", usuario));
		   	
			modeloColuna coluna30  = new modeloColuna(null,null);;	        		   	
		   	coluna30.setHeader(descricaoColunas("int_010", usuario));
		   	
	    	modeloColuna coluna31 = new modeloColuna(null, null); 
	    	coluna31.setHeader(descricaoColunas("dec_001", usuario));
					
			modeloColuna coluna32 = new modeloColuna(null,null);	        		   	
		   	coluna32.setHeader(descricaoColunas("dec_002", usuario));
			   	   	
		   	modeloColuna coluna33  = new modeloColuna(null,null);;        		   	
		   	coluna33.setHeader(descricaoColunas("dec_003", usuario));
		   	
			modeloColuna coluna34  = new modeloColuna(null,null);;	        		   	
		   	coluna34.setHeader(descricaoColunas("dec_004", usuario));
		   	
		   	modeloColuna coluna35  = new modeloColuna(null,null);;	        		   	
		   	coluna35.setHeader(descricaoColunas("dec_005", usuario));
		   	
			modeloColuna coluna36  = new modeloColuna(null,null);;	        		   	
		   	coluna36.setHeader(descricaoColunas("dec_006", usuario));
		   	
			modeloColuna coluna37  = new modeloColuna(null,null);;	        		   	
		   	coluna37.setHeader(descricaoColunas("dec_007", usuario)); 
		   	
			modeloColuna coluna38  = new modeloColuna(null,null);;	        		   	
		   	coluna38.setHeader(descricaoColunas("dec_008", usuario));
		   	
		   	modeloColuna coluna39  = new modeloColuna(null,null);;	        		   	
		   	coluna39.setHeader(descricaoColunas("dec_009", usuario));
		   	
			modeloColuna coluna40  = new modeloColuna(null,null);;	        		   	
		   	coluna40.setHeader(descricaoColunas("dec_010", usuario));
		   	
			modeloColuna coluna41  = new modeloColuna(null,null);;	        		   	
		   	coluna41.setHeader(descricaoColunas("data_001", usuario));
		   	
	    	modeloColuna coluna42 = new modeloColuna(null, null); 
	    	coluna42.setHeader(descricaoColunas("data_002", usuario));
					
			modeloColuna coluna43 = new modeloColuna(null,null);	        		   	
		   	coluna43.setHeader(descricaoColunas("data_003", usuario));
			   	   	
		   	modeloColuna coluna44  = new modeloColuna(null,null);;        		   	
		   	coluna44.setHeader(descricaoColunas("data_004", usuario));
		   	
			modeloColuna coluna45  = new modeloColuna(null,null);;	        		   	
		   	coluna45.setHeader(descricaoColunas("data_005", usuario));
		   	
		   	modeloColuna coluna46  = new modeloColuna(null,null);;	        		   	
		   	coluna46.setHeader(descricaoColunas("data_006", usuario));
		   	
			modeloColuna coluna47  = new modeloColuna(null,null);;	        		   	
		   	coluna47.setHeader(descricaoColunas("data_007", usuario));
		   	
			modeloColuna coluna48  = new modeloColuna(null,null);;	        		   	
		   	coluna48.setHeader(descricaoColunas("data_008", usuario)); 
		   	
			modeloColuna coluna49  = new modeloColuna(null,null);;	        		   	
		   	coluna49.setHeader(descricaoColunas("data_009", usuario));
		   	
		    modeloColuna coluna50  = new modeloColuna(null,null);;	        		   	
		   	coluna50.setHeader(descricaoColunas("data_010", usuario));
		
		   	/**condição para carregar colunas dinamicas**/  	
		   	if(id_tipo_doc != null){
    		     	 		 
	    		 /**bloco para listar todos os objetos da tabela layout que estejam com o campo flag com valor true**/
	    		 for(Layout_Empresa layout_Empresa : layout_EmpresaRN.listarCamposFlag(id_tipo_doc,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade())){
	    			 /**objeto array para adicionar todos os campos sequencia do bloco **/
	    		
	    			 listaId.add(layout_Empresa.getSequencia());
	    		 }	   			
    		
	    		 /**método que organiza todos os campos sequencias adicionados em ordem crescente**/
	    		
    		     		
	    		 /**bloco para listar todos os objetos da listaId**/
	    		 for(Integer id : listaId){
	    				    			 
	    			 try{
	    				 	    					    				 
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_001",usuario,id)){	  
		 		    		
		 		    		coluna.setProperty("char_001");    
		 		    		columns.add(coluna);	    	        	   
		 		    	} 
			 		    	 
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_002",usuario,id)){	  
		 		    		
		 		    		coluna2.setProperty("char_002");    	  
		 		   		    columns.add(coluna2);	
		 		    	} 
	 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"char_003",usuario,id)){
		 		   		 	
		 		     		coluna3.setProperty("char_003"); 
		 		   		 	columns.add(coluna3);
		 			   	} 
	 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"char_004",usuario,id)){	  
		 		    		 
		 		    		coluna4.setProperty("char_004"); 		
		 		    		columns.add(coluna4);
		 		    	} 
	 	   	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_005",usuario,id)){	  
		 		   		 	
		 		    		coluna5.setProperty("char_005"); 
		 		   		 	columns.add(coluna5);
		 		    	} 
	 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_006",usuario,id)){	
		 		    		
		 		   		 	coluna6.setProperty("char_006"); 
		 		   		 	columns.add(coluna6);
		 			   	} 
	 		    		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"char_007",usuario,id)){	  
		 		    		 
		 		    		coluna7.setProperty("char_007"); 		
		 		    		columns.add(coluna7);
		 		    	} 
	 	   	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_008", usuario, id)){	  
		 		   		 	
		 		    		coluna8.setProperty("char_008"); 
		 		   		 	columns.add(coluna8);
		 		    	} 
	 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_009", usuario, id)){	  
		 		   		 	
		 		    		coluna9.setProperty("char_009"); 
		 		   		 	columns.add(coluna9);
		 			   	} 
	 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_010",usuario, id)){
		 		    		
		 		   		 	coluna10.setProperty("char_010"); 
		 		   		 	columns.add(coluna10);
		 			   	} 
	 		   
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_011", usuario, id)){	  
		 		    		 
		 		    		coluna11.setProperty("char_011"); 		
		 		    		columns.add(coluna11);
		 		    	} 
	 	   	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_012", usuario, id)){
		 		   		 	
		 		    		coluna12.setProperty("char_012"); 
		 		   		 	columns.add(coluna12);
		 		    	} 
	 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_013", usuario, id)){
		 		    		
		 		   		 	coluna13.setProperty("char_013"); 
		 		   		 	columns.add(coluna13);
		 			   	} 
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"char_014", usuario, id)){	  
		 		    		 
		 		    		coluna14.setProperty("char_014"); 		
		 		    		columns.add(coluna14);
		 		    	} 
	 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_015", usuario, id)){	  
		 		   		 	
		 		    		coluna15.setProperty("char_015"); 
		 		   		 	columns.add(coluna15);
		 		    	} 
	 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_016", usuario, id)){	  
		 		   		 	
		 		    		coluna16.setProperty("char_016"); 
		 		   		 	columns.add(coluna16);
		 			   	} 
	 		   
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"char_017", usuario, id)){	  
		 		    		 
		 		    		coluna17.setProperty("char_017"); 		
		 		    	    columns.add(coluna17);
		 		    	} 
		 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_018", usuario, id)){	  
		 		   		 	
		 		    		coluna18.setProperty("char_018"); 
		 		   		 	columns.add(coluna18);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_019", usuario, id)){	 
		 		    		
		 		   		 	coluna19.setProperty("char_019"); 
		 		   		 	columns.add(coluna19);
		 			   	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"char_020", usuario, id)){	 
		 		    		
		 		   		 	coluna20.setProperty("char_020"); 
		 		   		 	columns.add(coluna20);
		 			   	} 
		 		   
		 		    
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_001", usuario, id)){	  
		 		    		
		 		    		coluna21.setProperty("int_001"); 		
		 		    		columns.add(coluna21);
		 		    	} 
		 	   	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_002", usuario, id)){
		 		   		 	
		 		    		coluna12.setProperty("int_002"); 
		 		   		 	columns.add(coluna22);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_003", usuario, id)){
		 		    		
		 		   		 	coluna21.setProperty("int_003"); 
		 		   		 	columns.add(coluna23);
		 			   	} 
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"int_004", usuario, id)){
		 		    		 
		 		    		coluna24.setProperty("int_004"); 		
		 		    		columns.add(coluna24);
		 		    	} 
		 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_005", usuario, id)){
		 		   		 	
		 		    		coluna25.setProperty("int_005"); 
		 		   		 	columns.add(coluna25);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_006", usuario, id)){
		 		    		
		 		   		 	coluna26.setProperty("int_006"); 
		 		   		 	columns.add(coluna26);
		 			   	} 
		 		   
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"int_007", usuario, id)){
		 		    		 
		 		    		coluna27.setProperty("int_007"); 		
		 		    		columns.add(coluna27);
		 		    	} 
		 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_008", usuario, id)){
		 		   		 	
		 		    		coluna28.setProperty("int_008"); 
		 		   		 	columns.add(coluna28);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_009", usuario, id)){
		 		    		
		 		   		 	coluna29.setProperty("int_009"); 
		 		   		 	columns.add(coluna29);
		 			   	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_010", usuario, id)){	 
		 		    		
		 		   		 	coluna30.setProperty("int_010"); 
		 		   		 	columns.add(coluna30);
		 			   	} 
		 		   
		 		    	if(comparaFlagCampo(id_tipo_doc,"dec_001", usuario, id)){
		 		    		 
		 		    		coluna31.setProperty("dec_001"); 		
		 		    		columns.add(coluna31);
		 		    	} 
		 	   	
		 		    	if(comparaFlagCampo(id_tipo_doc,"dec_002", usuario, id)){	  
		 		   		 	
		 		    		coluna32.setProperty("dec_002"); 
		 		   		 	columns.add(coluna32);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"dec_003", usuario, id)){	
		 		    		
		 		   		 	coluna33.setProperty("dec_003"); 
		 		   		 	columns.add(coluna33);
		 			   	} 
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"dec_004", usuario, id)){	  
		 		    		 
		 		    		coluna34.setProperty("dec_004"); 		
		 		    		columns.add(coluna34);
		 		    	} 
		 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"dec_005", usuario, id)){
		 		   		 	
		 		    		coluna35.setProperty("dec_005"); 
		 		   		 	columns.add(coluna35);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"dec_006", usuario, id)){	
		 		    		
		 		   		 	coluna36.setProperty("dec_006"); 
		 		   		 	columns.add(coluna36);
		 			   	} 
		 		   
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"dec_007", usuario, id)){	  
		 		    		 
		 		    		coluna37.setProperty("dec_007"); 		
		 		    		columns.add(coluna37);
		 		    	} 
		 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"dec_008", usuario, id)){	  
		 		   		 	
		 		    		coluna38.setProperty("dec_008"); 
		 		   		 	columns.add(coluna38);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"dec_009", usuario, id)){
		 		    		
		 		   		 	coluna39.setProperty("dec_009"); 
		 		   		 	columns.add(coluna39);
		 			   	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"int_010", usuario, id)){
		 		   		 	coluna40.setProperty("dec_010"); 
		 		   		 	columns.add(coluna40);
		 			   	} 
		 		   
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_001", usuario, id)){	  
		 		    		 
		 		    		coluna41.setProperty("data_001"); 		
		 		    		columns.add(coluna41);
		 		    	} 
		 	   	
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_002", usuario, id)){	  
		 		   		 	
		 		    		coluna42.setProperty("data_002"); 
		 		   		 	columns.add(coluna42);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_003", usuario, id)){
		 		    		
		 		   		 	coluna43.setProperty("data_003"); 
		 		   		 	columns.add(coluna43);
		 			   	} 
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"data_004", usuario, id)){
		 		    		 
		 		    		coluna44.setProperty("data_004"); 		
		 		    		columns.add(coluna44);
		 		    	} 
		 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_005", usuario, id)){	  
		 		   		 	
		 		    		coluna45.setProperty("data_005"); 
		 		   		 	columns.add(coluna45);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_006", usuario, id)){	
		 		    		
		 		   		 	coluna46.setProperty("data_006"); 
		 		   		 	columns.add(coluna46);
		 			   	} 
		 		   
		 		    	
		 		     	if(comparaFlagCampo(id_tipo_doc,"data_007", usuario, id)){	  
		 		    		 
		 		    		coluna47.setProperty("data_007"); 		
		 		    		columns.add(coluna47);
		 		    	} 
		 	  	
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_008", usuario, id)){	  
		 		   		 	
		 		    		coluna48.setProperty("data_008"); 
		 		   		 	columns.add(coluna48);
		 		    	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_009", usuario, id)){
		 		    		
		 		   		 	coluna49.setProperty("data_009"); 
		 		   		 	columns.add(coluna49);
		 			   	} 
		 		    	
		 		    	if(comparaFlagCampo(id_tipo_doc,"data_010", usuario, id)){
		 		    		
		 		   		 	coluna50.setProperty("data_010"); 
		 		   		 	columns.add(coluna50);
		 			   	} 
		    			 
    			 }catch(NullPointerException e){
    				
    				 return null;
    			 }   	
 		    	 		    	
    		}
	   
        		return columns;
         }
	  }
		return null;
    }

	
	
	public TreeNode getNodePrincipal() throws IllegalArgumentException, IllegalAccessException, ParseException {
		
		nodePrincipal = nodeDocumento.criarDocumento(pesquisarRastreabilidadePorIdDoc());

		return nodePrincipal;
	}

	public BigInteger pesquisarRastreabilidadePorIdDoc() {

		DocumentoRN documentoRN = new DocumentoRN();
		
		if (id != null && documentoRN.carregar(id) != null) {

			id_tipo_doc = documentoRN.carregar(id).getId_tipo_doc().getId_tipo_doc();
			return id;

		} else {

			id = null;
			return id;
		}
	}
		

	public String redirecionaDownloadConsulta() {

		this.documentoSelecionado = new Documento();
	
	

		try {

			if (this.nodeselecionado.getParent().getParent().getData().toString().equals("ROOT")) {

				return "/restrito/download_consulta.xhtml?iddoc=" + id + "&id=" + id + "faces-redirect=true";
			}

			if (this.nodeselecionado.getType().contains("trans_doc_arq")) {

				return "/restrito/download_consulta.xhtml?idtr=" + this.nodeselecionado.getData().toString() + "&id="
						+ id + "faces-redirect=true";
			}

			if (this.nodeselecionado.getType().contains("val_camp_arq")) {

				return "/restrito/download_consulta.xhtml?idca=" + this.nodeselecionado.getData().toString() + "&id="
						+ id + "faces-redirect=true";
			}

		} catch (NullPointerException e) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "Valor nao selecionado"));

		}
		return null;
	}

	public List<Documento> listarDownloadConsulta(BigInteger id_doc) {

		DocumentoRN documentoRN = new DocumentoRN();
		return documentoRN.listarPorIdDoc(id_doc);
	}

	public void downloadConsulta() {

		if (id_doc != null) {

			DocumentoRN documentoRN = new DocumentoRN();

			documento = documentoRN.carregar(id_doc);

			InputStream in = new ByteArrayInputStream(documento.getArquivo());

			streamedContent = new DefaultStreamedContent(in, documento.getExtensao_arq(), documento.getNome_arquivo());
		}

		if (id_trans_doc != null) {

			Transacao_Documento transacao_Documento = new Transacao_Documento();
			Transacao_DocumentoRN transacao_DocumentoRN = new Transacao_DocumentoRN();

			transacao_Documento = transacao_DocumentoRN.carregar(id_trans_doc);

			InputStream in = new ByteArrayInputStream(transacao_Documento.getArquivo());
			this.streamedContent = new DefaultStreamedContent(in, transacao_Documento.getExtensaoarq(),
			transacao_Documento.getNome_arquivo());

		}

		if (id_val_camp_trans_doc != null) {

			Val_Campos_Trans_Doc val_Campos_Trans_Doc = new Val_Campos_Trans_Doc();

			Val_Campos_Trans_DocRN val_Campos_Trans_DocRN = new Val_Campos_Trans_DocRN();

			val_Campos_Trans_Doc = val_Campos_Trans_DocRN.carregar(id_val_camp_trans_doc);

			InputStream in = new ByteArrayInputStream(val_Campos_Trans_Doc.getArquivo());

			this.streamedContent = new DefaultStreamedContent(in, val_Campos_Trans_Doc.getExtensaoarq(),
			val_Campos_Trans_Doc.getNome_arquivo());

		}

	}

	public String redirecionaRastreabilidade() {
			
		return "/restrito/rastreabilidade/rastreabilidade.xhtml?id=" + id + "faces-redirect=true";
	}

	/** fim metodos para rastreabilidade de documentos **/

	/** metodos para cadastro de documentos **/
	public List<Documento> listarPorIdTipoDocCodEmpCodFiCodUni(Usuario usuario) throws IndexOutOfBoundsException {
	
		DocumentoRN documentoRN = new DocumentoRN();		
		Tipo_DocumentoRN tipo_DocumentoRN = new Tipo_DocumentoRN();
		Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN();
		List<Documento> listaDocPorTipoUsuario = new ArrayList<Documento>();
		Usuario_Tipo_DocumentoRN usuario_Tipo_DocumentoRN = new Usuario_Tipo_DocumentoRN();
		Field field;
	
		retornaLogin(usuario);		
	
		if (usuario != null) {
					
			tipo_Documento = tipo_DocumentoRN.carregarPorIdTiDocCoDEmCodFiCodUni(id_tipo_doc,
					usuario.getCod_empresa().getCod_empresa(), 
					usuario.getCod_filial().getCod_filial(),
					usuario.getCod_unidade().getCod_unidade());
		
			if (id_tipo_doc != null) {
			
				try {

					if (isLinhaSelecionada()) {
						
						id_doc = documentoRN.listarPorIdTipoDocCodEmpCodFiCodUni(id_tipo_doc,
								usuario.getCod_empresa().getCod_empresa(), usuario.getCod_filial().getCod_filial(),
								usuario.getCod_unidade().getCod_unidade()).get(0).getId_doc();
					
						/**id_trans_doc = transacao_DocumentoRN.listarPorIdDoc(id_doc,
								usuario.getCod_empresa().getCod_empresa(), usuario.getCod_filial().getCod_filial(),
								usuario.getCod_unidade().getCod_unidade()).get(0).getId_transacao_doc();**/
						
					}

				} catch (Exception e) {
					
					this.id_doc = null;
					e.printStackTrace();
				}
			
				for(Documento documento : documentoRN.listarPorCodEmCodFiCodUni(usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade())) {
									
						for(Usuario_Tipo_Documento usuario_Tipo_Documento : usuario_Tipo_DocumentoRN.listarPorIdUsuarioCodEmCodFiCodUni(usuario.getId_usuario(),usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade())) {
							
							for(Layout_Empresa layout_Empresa : layout_EmpresaRN.listarPor_tipoDocumento(usuario_Tipo_Documento.getId_tipo_doc().getId_tipo_doc())) {
								
							try {
								
								field = documento.getClass().getDeclaredField(layout_Empresa.getCod_campo());
								field.setAccessible(true);
							    							
								if(usuario_Tipo_Documento.getConteudo().equals("")) {
									 return documentoRN.listarPorIdTipoDocCodEmpCodFiCodUni(id_tipo_doc,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade());									
								}
								
								if(layout_Empresa.getDescricao().equals(usuario_Tipo_Documento.getCod_campo()) && usuario_Tipo_Documento.getConteudo().equals(field.get(documento))) {
																	
									listaDocPorTipoUsuario.add(documento);
								}
																
							} catch (NoSuchFieldException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
							}
						
						}
						
					}
					
				}
			
				if(listaDocPorTipoUsuario.isEmpty()) {
				 return documentoRN.listarPorIdTipoDocCodEmpCodFiCodUni(id_tipo_doc,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade());
				}else {
					return listaDocPorTipoUsuario;
				}
				
					
					
				
							
		}else {
		
			return null;
		}
		}
	
		return null;
	}

	public List<Documento> listarPorCodEmpCodFiCodUni(Usuario usuario) {

		DocumentoRN documentoRN = new DocumentoRN();

		return documentoRN.listarPorCodEmCodFiCodUni(usuario.getCod_empresa().getCod_empresa(),
				usuario.getCod_filial().getCod_filial(), usuario.getCod_unidade().getCod_unidade());

	}

	public BigInteger selecionarLinhaDoc(SelectEvent event) {

		this.idDocSelecionado = BigInteger.valueOf(Long.parseLong(((Documento) event.getObject()).getId_doc().toString()));
	    		
		linhaSelecionada = false;
		
		id_trans_doc = null;
		
		return this.idDocSelecionado;
	}

	public BigInteger selecionarLinhaDocDetalhe(SelectEvent event) {
		
		this.idDocDetalhe = BigInteger.valueOf(Long.parseLong(((Documento) event.getObject()).getId_doc().toString()));
		
		return this.idDocDetalhe;
	}

	public String novo() {
        
		return "/restrito/documento/cadastro_documento.xhtml?id=" + id_tipo_doc + " faces-redirect=true";
	}

	public String alterar() {

		if (documentoSelecionado == null) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, "documento nao foi selecionado!"));
			return null;

		} else {

			return "/restrito/documento/cadastro_documento.xhtml?doc=" + documentoSelecionado.getId_doc() + "&id="
					+ documentoSelecionado.getId_tipo_doc().getId_tipo_doc() + "faces-redirect=true";
		}
	}

	public String excluir() {

		DocumentoRN documentoRN = new DocumentoRN();

		this.documento = documentoRN.carregar(this.documentoSelecionado.getId_doc());

		try {

			id_tipo_doc = this.documento.getId_tipo_doc().getId_tipo_doc();
			documentoRN.excluir(this.documento);

			return "/restrito/documento/documento.xhtml?id=" + id_tipo_doc + "faces-redirect=true";

		} catch (DAOException e) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", e.getMessage()));
			return null;
		}

	}

	public Documento iniciar(Usuario usuario) throws IllegalArgumentException, IllegalAccessException {

		DocumentoRN documentoRN = new DocumentoRN();
		Integer incremento;
		Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN();
	
						
		if (id == null) {
			
			this.documento.getCod_empresa().setCod_empresa(usuario.getCod_empresa().getCod_empresa());
			this.documento.getCod_filial().setCod_filial(usuario.getCod_filial().getCod_filial());
			this.documento.getCod_unidade().setCod_unidade(usuario.getCod_unidade().getCod_unidade());
			this.documento.getId_tipo_doc().setId_tipo_doc(id_tipo_doc);
						
			 try{
						
				incremento = documentoRN.listarUltimoRegistro(id_tipo_doc,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade()).getInt_001();
				
				if(layout_EmpresaRN.carregarPorCodCampo("int_001",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_001",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
				
					if(incremento == null){		
				
						this.documento.setInt_001(1);
					
					}else{
					
						this.documento.setInt_001(incremento + 1);
					}						
					
				}
			
	       		if(layout_EmpresaRN.carregarPorCodCampo("int_002",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_002",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
						       			
	       			if(incremento == null){
						this.documento.setInt_002(1);
					}else{
						this.documento.setInt_002(incremento + 1);
					}			
					desabilitaCampoIncremental("int_002",documento);
				}
			
				if(layout_EmpresaRN.carregarPorCodCampo("int_003",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_003",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					
					if(incremento == null){
						this.documento.setInt_003(1);
					}else{
						this.documento.setInt_003(incremento + 1);
					}			
					desabilitaCampoIncremental("int_003",documento);
				}
				
				if(layout_EmpresaRN.carregarPorCodCampo("int_004",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_004",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					if(incremento == null){
						this.documento.setInt_004(1);
					}else{
						this.documento.setInt_004(incremento + 1);
					}			
					desabilitaCampoIncremental("int_004",documento);
				}
				
				if(layout_EmpresaRN.carregarPorCodCampo("int_005",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_005",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					if(incremento == null){
						this.documento.setInt_005(1);
					}else{
						this.documento.setInt_005(incremento + 1);
					}			
					desabilitaCampoIncremental("int_005",documento);
				}
				
				if(layout_EmpresaRN.carregarPorCodCampo("int_006",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_006",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					if(incremento == null){
						this.documento.setInt_006(1);
					}else{
						this.documento.setInt_006(incremento + 1);
					}			
					desabilitaCampoIncremental("int_006",documento);
				}
				
				if(layout_EmpresaRN.carregarPorCodCampo("int_007",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_007",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					if(incremento == null){
						this.documento.setInt_007(1);
					}else{
						this.documento.setInt_007(incremento + 1);
					}			
					desabilitaCampoIncremental("int_007",documento);
				}
				
				if(layout_EmpresaRN.carregarPorCodCampo("int_008",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_008",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					if(incremento == null){
						this.documento.setInt_008(1);
					}else{
						this.documento.setInt_008(incremento + 1);
					}			
					desabilitaCampoIncremental("int_008",documento);
				}
				
				if(layout_EmpresaRN.carregarPorCodCampo("int_009",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_009",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					if(incremento == null){
						this.documento.setInt_009(1);
					}else{
						this.documento.setInt_009(incremento + 1);
					}			
					desabilitaCampoIncremental("int_009",documento);
				}

				if(layout_EmpresaRN.carregarPorCodCampo("int_010",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()) != null && layout_EmpresaRN.carregarPorCodCampo("int_010",documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
					if(incremento == null){
						this.documento.setInt_010(1);
					}else{
						this.documento.setInt_010(incremento + 1);
					}			
					desabilitaCampoIncremental("int_010",documento);
				}
			  
				
				
			}catch(NullPointerException e){			
			  
				this.documento.setInt_001(1);
		
			}
			
			
		} else {
			
			this.documento = documentoRN.carregar(id);
			
			return this.documento;
		}
		
		return this.documento;	

	}

	public boolean desabilitarCampos() {

		if (this.documentoSelecionado.getId_doc() == null) {

			return false;

		} else {

			return true;
		}
	}

	public String descricaoDeAnexo(Documento documento) {

		if (documento.getNome_arquivo() != "") {

			return documento.getNome_arquivo();

		} else {

			return "Upload";
		}
	}

	/**Método para verficar se existem valores cadastrados para os campos da tabela documento**/
	public List<Grupo_Valores_Possiveis_Doc> verificaValorPos(String cod_campo) {

		Val_Campos_DocRN val_Campos_DocRN = new Val_Campos_DocRN();
		Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN();
		Layout_Empresa layoutEmpresa = new Layout_Empresa();
		Grupo_Valores_Possiveis_DocRN grupo_Valores_Possiveis_DocRN = new Grupo_Valores_Possiveis_DocRN();

		try {
			
			layoutEmpresa = layout_EmpresaRN.listarPorIdTipoDocCodCampo(id_tipo_doc, cod_campo,
							documento.getCod_empresa().getCod_empresa(), documento.getCod_filial().getCod_filial(),
							documento.getCod_unidade().getCod_unidade());
			
			if(layoutEmpresa != null){
				
			
				for (Val_Campos_Doc val_Campos_Doc : val_Campos_DocRN.listarPorIdTipoDocIdLay(
						layoutEmpresa.getId_tipo_doc().getId_tipo_doc(), layoutEmpresa.getId_layout(),
						layoutEmpresa.getCod_empresa().getCod_empresa(), layoutEmpresa.getCod_filial().getCod_filial(),
						layoutEmpresa.getCod_unidade().getCod_unidade())) {
				
				return grupo_Valores_Possiveis_DocRN.listarPorIdGrupoValoresCodEmCodFiCodUni(
						val_Campos_Doc.getId_grupo_Valores().getId_grupo_valores(),
						val_Campos_Doc.getCod_empresa().getCod_empresa(),
						val_Campos_Doc.getCod_filial().getCod_filial(),
						val_Campos_Doc.getCod_unidade().getCod_unidade());
				
				}
			}

		}catch (NullPointerException e) {
            
			e.printStackTrace();
			
		}
	
		return null;
	}

	public String tamanhoNomeArquivo(String label) {

		try {

			if (label.isEmpty()) {
				return "Upload";
			} else {

				if (label.length() >= 15) {

					return label.substring(0, 15);
				}
				return label;
			}
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

	}

	public boolean filtro(String label, Usuario usuario) {

		Layout_EmpresaRN layout_Empresa2RN = new Layout_EmpresaRN();

		if (layout_Empresa2RN.carregarPorCodCampo(label, usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(), usuario.getCod_unidade().getCod_unidade()) == null) {

			return false;

		} else {

			return true;
		}
	}

	public boolean filtro2(String label) {

		Layout_EmpresaRN layoutRN = new Layout_EmpresaRN();

		if (layoutRN.listarPorIdTipoDocCodCampo(id_tipo_doc, label, this.documento.getCod_empresa().getCod_empresa(),
				this.documento.getCod_filial().getCod_filial(),
				this.documento.getCod_unidade().getCod_unidade()) != null) {

			return true;

		} else {

			return false;
		}

	}

	public boolean desabilitaCampoFlag(String label) {

		Layout_EmpresaRN layoutRN = new Layout_EmpresaRN();

		if (layoutRN
				.listarPorIdTipoDocCodCampo(id_tipo_doc, label, documento.getCod_empresa().getCod_empresa(),
						documento.getCod_filial().getCod_filial(), documento.getCod_unidade().getCod_unidade())
				.isFlag_campo()) {

			return true;

		} else {

			return false;
		}

	}

	public String descricaoColunas(String label, Usuario usuario) {

		Layout_EmpresaRN layoutEmpresaRN = new Layout_EmpresaRN();

		for (Layout_Empresa layout : layoutEmpresaRN.listarPorIdTipoDoc(id_tipo_doc,
				usuario.getCod_empresa().getCod_empresa(), usuario.getCod_filial().getCod_filial(),
				usuario.getCod_unidade().getCod_unidade())) {

			if (label.contentEquals(layout.getCod_campo())) {

				if(layout.isFiltro()){
					return layout.getDescricao() + "*";
				}else{
					return layout.getDescricao(); 
				}
			}

		}

		return null;
	}

	public String salvar() throws DAOException {
		
	
		DocumentoRN documentoRN = new DocumentoRN();
	
		if (id == null) {
					
			if (arquivoDoc.getFileName().contains(".ppt")) {
				this.documento.setExtensao_arq(".ppt");
			}
			
			if (arquivoDoc.getFileName().contains(".pptx")) {
				this.documento.setExtensao_arq(".pptx");
			}
			
			if (arquivoDoc.getFileName().contains(".pdf")) {
				this.documento.setExtensao_arq(".pdf");
			}

			else if (arquivoDoc.getFileName().contains(".xlsx")) {
				this.documento.setExtensao_arq(".xlsx");
			}

			else if (arquivoDoc.getFileName().contains(".docx")) {
				this.documento.setExtensao_arq(".docx");

			} else if (arquivoDoc.getFileName().contains(".txt")) {
				this.documento.setExtensao_arq(".txt");

			} else if (arquivoDoc.getFileName().contains(".htm")) {
				this.documento.setExtensao_arq(".html");

			} else if (arquivoDoc.getFileName().contains(".jpeg")) {
				this.documento.setExtensao_arq(".jpeg");

			} else if (arquivoDoc.getFileName().contains(".png")) {
				this.documento.setExtensao_arq(".png");

			} else if (arquivoDoc.getFileName().contains(".bmp")) {
				this.documento.setExtensao_arq(".bmp");
			}

			else if (arquivoDoc.getFileName().contains(".zip")) {
				this.documento.setExtensao_arq(".zip");
			}
			else if (arquivoDoc.getFileName().contains(".rar")) {
				this.documento.setExtensao_arq(".rar");
			}
			
			this.documento.setNome_arquivo(arquivoDoc.getFileName());

			try {
				
				this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));

			} catch (IOException e) {

				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", e.getMessage()));
			}
				
				documentoRN.salvar(this.documento);					
				this.documentoSelecionado = new Documento();
				id_tipo_doc = null;				
				id = null;
								
				return "/restrito/documento/documento.xhtml?id=" + this.documento.getId_tipo_doc().getId_tipo_doc() + "faces-redirect=true";
								
		} else {

			try {
				
				if (arquivoDoc.getFileName().contains(".ppt") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".ppt");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
				}

				if (arquivoDoc.getFileName().contains(".pptx") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".pptx");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
				}

				if (arquivoDoc.getFileName().contains(".pdf") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".pdf");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
				}
			
				if (arquivoDoc.getFileName().contains(".pdf") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".pdf");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
				}

				else if (arquivoDoc.getFileName().contains(".xlsx") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".xlsx");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
					
				} else if (arquivoDoc.getFileName().contains(".doc") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".doc");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));

				} else if (arquivoDoc.getFileName().contains(".docx") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".docx");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));

				} else if (arquivoDoc.getFileName().contains(".txt") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".txt");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
				} else if (arquivoDoc.getFileName().contains(".htm") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".html");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));

				} else if (arquivoDoc.getFileName().contains(".jpeg") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".jpeg");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));

				} else if (arquivoDoc.getFileName().contains(".png") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".png");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));

				} else if (arquivoDoc.getFileName().contains(".bmp") && arquivoDoc != null) {

					this.documento.setExtensao_arq(".bmp");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));

				}
				
				else if (arquivoDoc.getFileName().contains(".zip")) {
					
				 	this.documento.setExtensao_arq(".zip");
					this.documento.setNome_arquivo(arquivoDoc.getFileName());
					this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
				}
				
				else if (arquivoDoc.getFileName().contains(".rar")) {
					
					 	this.documento.setExtensao_arq(".rar");
						this.documento.setNome_arquivo(arquivoDoc.getFileName());
						this.documento.setArquivo(IOUtils.toByteArray(arquivoDoc.getInputstream()));
				}

			} catch (IOException e) {

				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", e.getMessage()));

				return null;
			}
			
			
			documentoRN.alterar(this.documento);
			this.documentoSelecionado = new Documento();
			id_tipo_doc = this.documento.getId_tipo_doc().getId_tipo_doc();
			id = null;
			this.documento = new Documento();
		
			return "/restrito/documento/documento.xhtml?id=" + id_tipo_doc + "faces-redirect=true";
		}
		
		
	}

	public String redirecionaCadastro() {

		return "/restrito/documento/documento.xhtml?id=" + id_tipo_doc + "faces-redirect=true";

	}

	public String redirecionaRastreabilidadePorDocumento() {

		if (this.documentoSelecionado != null) {

			return "/restrito/rastreabilidade/rastreabilidade.xhtml?id=" + documentoSelecionado.getId_doc()
					+ "faces-redirect=true";

		} else {

			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "Documento n�o foi selecionado!"));
			return null;
		}
	}

	public String redirecionaConsultaUsuario() {

		return "/restrito/rastreabilidade/documento_tipo_usuario.xhtml?id=" + id_tipo_doc + "faces-redirect=true";

	}

	public String redirecionaMenu() {
		
		this.documento = new Documento();
		this.documentoSelecionado = new Documento();
		this.documentoSelecionado = new Documento();
		this.id_tipo_doc = null;
		id = null;		
		this.id_doc = null;
		id_trans_doc = null;

		return "/restrito/menu.xhtml?faces-redirect=true";
	}

	public StreamedContent getStreamedContentDoc() {

		DocumentoRN documentoRN = new DocumentoRN();

		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idDoc");
		long idDoc = Long.parseLong(id);

		documento = documentoRN.carregar(BigInteger.valueOf(idDoc));

		if (documento.getArquivo() != null) {

			InputStream in = new ByteArrayInputStream(documento.getArquivo());

			this.streamedContentDoc = new DefaultStreamedContent(in, documento.getExtensao_arq(),
					documento.getNome_arquivo());

			return this.streamedContentDoc;

		} else {

			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "O arquivo nao existe"));
			return null;
		}
	}

	public StreamedContent GerarQRCODE() {

		String p_id_doc = idDocDetalhe.toString();

		String url = "https://optweb-3.herokuapp.com/restrito/rastreabilidade/rastreabilidade.xhtml?id=" + p_id_doc;

		try {

			QRCodeWriter qrCodeWriter = new QRCodeWriter();

			BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 350, 350);

			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

			InputStream in = new ByteArrayInputStream(pngOutputStream.toByteArray());

			StreamedContent streamedContent = new DefaultStreamedContent(in, ".png", "documento.png");

			return streamedContent;

		} catch (IOException e) {

			e.printStackTrace();
			return null;

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

	}

	public String verificaArquivoAnexado(String arquivo) {

		if (arquivo.isEmpty()) {
			return "";

		} else {

			return "/imagens/file.png";
		}

	}
	
	
	public boolean desabilitaCampoIncremental(String cod_campo,Documento documento){
		
		Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN();
	
		try{
			
			
		if(documento != null && layout_EmpresaRN.listarPorIdTipoDocCodCampo(id_tipo_doc, cod_campo,documento.getCod_empresa().getCod_empresa(),documento.getCod_filial().getCod_filial(),documento.getCod_unidade().getCod_unidade()).isFlagCampos()){
			
			desabilitaCampoIncremento = true;
			return desabilitaCampoIncremento;
			
		}else{
			
			desabilitaCampoIncremento = false;
			return desabilitaCampoIncremento;
		}		}
		catch(NullPointerException e){
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	   public List<Documento> listarCampos(Usuario usuario){
			
		DocumentoRN documentoRN = new DocumentoRN();				
		return documentoRN.listarPorIdTipoDoc(id_tipo_doc);	

	   }
	   	   	
	   public StreamedContent GerarQRCODEPorCamposDoc() {
		
		   		   
			try {
														
				QRCodeWriter qrCodeWriter = new QRCodeWriter();

				BitMatrix bitMatrix = qrCodeWriter.encode(adicionarValorUrl(), BarcodeFormat.QR_CODE, 350, 350);

				ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

				MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

				InputStream in = new ByteArrayInputStream(pngOutputStream.toByteArray());

				
				StreamedContent streamedContent = new DefaultStreamedContent(in, ".png", "documento.png");

				return streamedContent;

			} catch (IOException e) {

				e.printStackTrace();
				return null;

			} catch (Exception e) {

				e.printStackTrace();
				return null;
			}
	}
	   
	   public Boolean carregarCampoPorQrCode(String cod_campo){
		   
		   Layout_EmpresaRN layout_EmpresaRN = new Layout_EmpresaRN();
		   
		  try{ 
					
			   if(layout_EmpresaRN.listarPorIdTipoDocCodCampo(id_tipo_doc,cod_campo,usuario.getCod_empresa().getCod_empresa(),usuario.getCod_filial().getCod_filial(),usuario.getCod_unidade().getCod_unidade()).isQrcode()){
				   
				   return true;
				   
			   }else{
				   return false;
			   }
			   
		  }catch(NullPointerException e){
			  
			  return false;
		  }
	}
	   
	public String adicionarValorUrl(){
			   
		   String url = "http://optweb-3.herokuapp.com/restrito/rastreabilidade/rastreabilidade.xhtml?tipo="; 
		
		   url = url + String.valueOf(id_tipo_doc) + "&val=";
	
		   for(Integer i = 0; i < columns.size();i++){
			   
			   	try {
				   
			   		Field campo = doc.getClass().getDeclaredField(columns.get(i).property);
			   		campo.setAccessible(true);
											
					if(i == 0){
										
						if(campo.getName().contains("data") && campo.get(doc) != null){
						
							url = url +  "," + String.valueOf(campo.get(doc));
						
						}else{					
					
							if(campo.get(doc) != null){
						
								url = url +  campo.get(doc).toString() + ",";
													
							}
						}					
					
					}else{
					
						if(campo.getName().contains("data") && campo.get(doc) != null){
						
							url = url +  campo.get(doc).toString() + ",";
						
						}else{
						
							if(campo.get(doc) != null){
							
								url = url + campo.get(doc).toString() + ",";
													
							}
						}
					
					}
				 				
			    } catch (NoSuchFieldException e) {				
					e.printStackTrace();
				} catch (SecurityException e) {			
					e.printStackTrace();
				} catch (IllegalArgumentException e) {					
					e.printStackTrace();
				}catch (NullPointerException e) {				
					e.printStackTrace();
				} catch (IllegalAccessException e) {					
					e.printStackTrace();
				}
			   	   
		   }
		
		   return  url;		   	  
	}
		
	public void setStreamedContentDoc(StreamedContent streamedContentDoc) {
		this.streamedContentDoc = streamedContentDoc;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public Documento getDocumentoSelecionado() {
		return documentoSelecionado;
	}

	public void setDocumentoSelecionado(Documento documentoSelecionado) {
		this.documentoSelecionado = documentoSelecionado;
	}

	public void setNodePrincipal(TreeNode nodePrincipal) {
		this.nodePrincipal = nodePrincipal;
	}

	public TreeNode getNodeselecionado() {
		return nodeselecionado;
	}

	public void setNodeselecionado(TreeNode nodeselecionado) {
		this.nodeselecionado = nodeselecionado;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getId_doc() {
		return id_doc;
	}

	public void setId_doc(BigInteger id_doc) {
		this.id_doc = id_doc;
	}

	public LazyDataModel<Documento> getLazymodel() {
		return lazymodel;
	}

	public void setLazymodel(LazyDataModel<Documento> lazymodel) {
		this.lazymodel = lazymodel;
	}

	public UploadedFile getArquivoDoc() throws SQLException, UtilException, IOException, JRException {

		return arquivoDoc;
	}

	public void setArquivoDoc(UploadedFile arquivoDoc) {

		this.arquivoDoc = arquivoDoc;
	}

	public StreamedContent getStreamedContent() {

		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public NodeDocumento getNodeDocumento() {
		return nodeDocumento;
	}

	public void setNodeDocumento(NodeDocumento nodeDocumento) {
		this.nodeDocumento = nodeDocumento;
	}

	public BigInteger getId_trans_doc() {
		return id_trans_doc;
	}

	public void setId_trans_doc(BigInteger id_trans_doc) {
		this.id_trans_doc = id_trans_doc;
	}

	public BigInteger getId_tipo_doc() {
		return id_tipo_doc;
	}

	public void setId_tipo_doc(BigInteger id_tipo_doc) {
		this.id_tipo_doc = id_tipo_doc;
	}

	public boolean isLinhaSelecionada() {
		return linhaSelecionada;
	}

	public void setLinhaSelecionada(boolean linhaSelecionada) {
		this.linhaSelecionada = linhaSelecionada;
	}

	public BigInteger getIdDocSelecionado() {
		return idDocSelecionado;
	}

	public void setIdDocSelecionado(BigInteger idDocSelecionado) {
		this.idDocSelecionado = idDocSelecionado;
	}

	public BigInteger getIdDocDetalhe() {
		return idDocDetalhe;
	}

	public void setIdDocDetalhe(BigInteger idDocDetalhe) {
		this.idDocDetalhe = idDocDetalhe;
	}

	public Integer getId_val_camp_trans_doc() {
		return id_val_camp_trans_doc;
	}

	public void setId_val_camp_trans_doc(Integer id_val_camp_trans_doc) {
		this.id_val_camp_trans_doc = id_val_camp_trans_doc;
	}

	public Tipo_Documento getTipo_Documento() {
		return tipo_Documento;
	}

	public void setTipo_Documento(Tipo_Documento tipo_Documento) {
		this.tipo_Documento = tipo_Documento;
	}


	public List<modeloColuna> getColumns() {
		return columns;
	}


	public void setColumns(List<modeloColuna> columns) {
		this.columns = columns;
	}	

	public void setDesabilitaCampoIncremento(boolean desabilitaCampoIncremento) {
		this.desabilitaCampoIncremento = desabilitaCampoIncremento;
	}

	public boolean isDesabilitaCampoIncremento() {
		return desabilitaCampoIncremento;
	}


	public Documento getDoc() {
		return doc;
	}

	public void setDoc(Documento doc) {
		this.doc = doc;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arquivoDoc == null) ? 0 : arquivoDoc.hashCode());
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + (desabilitaCampoIncremento ? 1231 : 1237);
		result = prime * result + ((documento == null) ? 0 : documento.hashCode());
		result = prime * result + ((documentoSelecionado == null) ? 0 : documentoSelecionado.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idDocDetalhe == null) ? 0 : idDocDetalhe.hashCode());
		result = prime * result + ((idDocSelecionado == null) ? 0 : idDocSelecionado.hashCode());
		result = prime * result + ((id_doc == null) ? 0 : id_doc.hashCode());
		result = prime * result + ((id_tipo_doc == null) ? 0 : id_tipo_doc.hashCode());
		result = prime * result + ((id_trans_doc == null) ? 0 : id_trans_doc.hashCode());
		result = prime * result + ((id_val_camp_trans_doc == null) ? 0 : id_val_camp_trans_doc.hashCode());
		result = prime * result + ((lazymodel == null) ? 0 : lazymodel.hashCode());
		result = prime * result + (linhaSelecionada ? 1231 : 1237);
		result = prime * result + ((nodeDocumento == null) ? 0 : nodeDocumento.hashCode());
		result = prime * result + ((nodePrincipal == null) ? 0 : nodePrincipal.hashCode());
		result = prime * result + ((nodeselecionado == null) ? 0 : nodeselecionado.hashCode());	
		result = prime * result + ((streamedContent == null) ? 0 : streamedContent.hashCode());
		result = prime * result + ((streamedContentDoc == null) ? 0 : streamedContentDoc.hashCode());
		result = prime * result + ((tipo_Documento == null) ? 0 : tipo_Documento.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
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
		DocumentoBean other = (DocumentoBean) obj;
		if (arquivoDoc == null) {
			if (other.arquivoDoc != null)
				return false;
		} else if (!arquivoDoc.equals(other.arquivoDoc))
			return false;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (desabilitaCampoIncremento != other.desabilitaCampoIncremento)
			return false;
		if (documento == null) {
			if (other.documento != null)
				return false;
		} else if (!documento.equals(other.documento))
			return false;
		if (documentoSelecionado == null) {
			if (other.documentoSelecionado != null)
				return false;
		} else if (!documentoSelecionado.equals(other.documentoSelecionado))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idDocDetalhe == null) {
			if (other.idDocDetalhe != null)
				return false;
		} else if (!idDocDetalhe.equals(other.idDocDetalhe))
			return false;
		if (idDocSelecionado == null) {
			if (other.idDocSelecionado != null)
				return false;
		} else if (!idDocSelecionado.equals(other.idDocSelecionado))
			return false;
		if (id_doc == null) {
			if (other.id_doc != null)
				return false;
		} else if (!id_doc.equals(other.id_doc))
			return false;
		if (id_tipo_doc == null) {
			if (other.id_tipo_doc != null)
				return false;
		} else if (!id_tipo_doc.equals(other.id_tipo_doc))
			return false;
		if (id_trans_doc == null) {
			if (other.id_trans_doc != null)
				return false;
		} else if (!id_trans_doc.equals(other.id_trans_doc))
			return false;
		if (id_val_camp_trans_doc == null) {
			if (other.id_val_camp_trans_doc != null)
				return false;
		} else if (!id_val_camp_trans_doc.equals(other.id_val_camp_trans_doc))
			return false;
		if (lazymodel == null) {
			if (other.lazymodel != null)
				return false;
		} else if (!lazymodel.equals(other.lazymodel))
			return false;
		if (linhaSelecionada != other.linhaSelecionada)
			return false;
		if (nodeDocumento == null) {
			if (other.nodeDocumento != null)
				return false;
		} else if (!nodeDocumento.equals(other.nodeDocumento))
			return false;
		if (nodePrincipal == null) {
			if (other.nodePrincipal != null)
				return false;
		} else if (!nodePrincipal.equals(other.nodePrincipal))
			return false;
		if (nodeselecionado == null) {
			if (other.nodeselecionado != null)
				return false;
		} else if (!nodeselecionado.equals(other.nodeselecionado))
			return false;		
		if (streamedContent == null) {
			if (other.streamedContent != null)
				return false;
		} else if (!streamedContent.equals(other.streamedContent))
			return false;
		if (streamedContentDoc == null) {
			if (other.streamedContentDoc != null)
				return false;
		} else if (!streamedContentDoc.equals(other.streamedContentDoc))
			return false;
		if (tipo_Documento == null) {
			if (other.tipo_Documento != null)
				return false;
		} else if (!tipo_Documento.equals(other.tipo_Documento))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}

}