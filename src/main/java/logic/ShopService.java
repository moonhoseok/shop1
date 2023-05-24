package logic;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dao.ItemDao;
import dao.SaleDao;
import dao.SaleItemDao;
import dao.UserDao;



@Service // @Component + Service(controller 기능과 dao 기능의 중간 역할)
public class ShopService {
	@Autowired // ItemDao 객체주입.
	private ItemDao itemdao;
	@Autowired //  객체주입.
	private UserDao userdao;
	@Autowired //  객체주입.
	private SaleDao saledao;
	@Autowired //  객체주입.
	private SaleItemDao saleItemdao;
	
	public List<Item> itemList(){
		return itemdao.list();
	}

	public Item getItem(Integer id) {

		return itemdao.getItem(id);
	}

	public void itemCreate(Item item, HttpServletRequest request) {
		if(item.getPicture() != null && !item.getPicture().isEmpty()) {
			// 업로드해야하는 파일이 있는 경우  
			String path = request.getServletContext().getRealPath("/")+"img/";
			uploadFileCreate(item.getPicture(),path);
			// 업로드된 파일이름
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		// db에 내용 저장
		int maxid = itemdao.maxId(); // item테이블에 저장된 최대id값
		item.setId(maxid+1); // 최대값보다 하나더 큰값 저장
		itemdao.insert(item); // db에 추가
	}
	//파일 업로드 부분
	private void uploadFileCreate(MultipartFile file, String path) {
		// file : 파일의 내용
		// path : 업로드할 폴더
		String orgFile = file.getOriginalFilename(); // 파일이름
		File f = new File(path);
		if(!f.exists())f.mkdirs();
		try {
			//file에 저장된 내용을 파일로 저장.
			file.transferTo(new File(path+orgFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void itemUpdate(@Valid Item item, HttpServletRequest request) {
		if(item.getPicture() != null && !item.getPicture().isEmpty()) {
			// 업로드해야하는 파일이 있는 경우  
			String path = request.getServletContext().getRealPath("/")+"img/";
			uploadFileCreate(item.getPicture(),path);
			// 업로드된 파일이름
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		itemdao.update(item);
		
	}

	public void itemDelete(Integer id) {
		itemdao.delete(id);
		
	}

	public void userinsert(User user) {
		userdao.insert(user);
		
	}

	public User selectUserOne(String userid) {
		
		return userdao.selectOne(userid);
	}
	/*
	 * 1. 로그인정보, 장바구니정보 => sale, saleitem 테이블의 데이터 저장
	 * 2. 결과는 Sale 객체에 저장
	 * 		-sale테이블 저장 : saleid값 구하기. 최대값 + 1
	 * 		- saleitem 테이블 저장 : Cart 데이터를 이용하여 저장
	 */
	public Sale checkend(User loginUser, Cart cart) {
		int maxsaleid = saledao.getMaxSaleId(); // saleid 최대값 조회
		Sale sale = new Sale();
		sale.setSaleid(maxsaleid+1);			// sale 객체 만들기	
		sale.setUser(loginUser);				// sale 객체 만들기
		sale.setUserid(loginUser.getUserid()); // sale 객체 만들기
		saledao.insert(sale); // sale 테이블에 데이터 추가
		int seq = 0;
		for(ItemSet is : cart.getItemSetList()) {
			SaleItem saleItem = new SaleItem(sale.getSaleid(),++seq,is);
			sale.getItemList().add(saleItem);
			saleItemdao.insert(saleItem); //saleitem 테이블에 데이터 추가
		}
		return sale; // 주문정보, 주문상품정보, 상품정보, 사용자정보
	}
	
	public List<Sale> salelist(String userid) {
		List<Sale> list = saledao.list(userid);//id 사용자가 주문 정복목록 
		for(Sale sa : list) {
			//saleitemlist : 한개의 주문에 해당하는 주문상품 목록
			List<SaleItem> saleitemlist =saleItemdao.list(sa.getSaleid());
			for(SaleItem si : saleitemlist) {
				Item item = itemdao.getItem(si.getItemid()); //상품정보
				si.setItem(item);
			}
			sa.setItemList(saleitemlist);
		}
		return list;
	}

	public void userupdate( User user) {
		userdao.update(user);
	}

	public void userdelete(String userid) {
		userdao.delete(userid);
	}

	public void userpassword(String userid, String chgpass) {
		userdao.pwupdate(userid, chgpass);
		
	}

}
