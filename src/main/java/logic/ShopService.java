package logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
