package logic;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dao.ItemDao;



@Service // @Component + Service(controller 기능과 dao 기능의 중간 역할)
public class ShopService {
	@Autowired // ItemDao 객체주입.
	private ItemDao itemdao;
	
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

}
