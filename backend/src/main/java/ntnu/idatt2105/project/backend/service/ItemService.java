package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.model.User;
import ntnu.idatt2105.project.backend.model.dto.ItemDTO;
import ntnu.idatt2105.project.backend.model.dto.Filter;
import ntnu.idatt2105.project.backend.model.Item;
import ntnu.idatt2105.project.backend.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Item Service class which contains methods used to retrieve and handle items from the database.
 */
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemDTO getItemById(Long id){
        Item item = itemRepository.findById(id).orElse(null);
        if(item == null){
            return null;
        }
        return new ItemDTO(item);
    }

    /**
     * Gets an item page, using the filter specified in the parameter. The method first gets the filtered
     * items from the database, then creates a Data Transfer Object for each of the items and maps them to a Page.
     * That page is returned.
     * @param pageNr
     * @param pageSize
     * @param filter
     * @return
     */
    public Page<ItemDTO> getItemPage(int pageNr, int pageSize, Filter filter){
        int minPrice = filter.getMinPrice();
        int maxPrice = filter.getMaxPrice();
        Page<Item> itemPage;
        if(minPrice == 0 && maxPrice == 0){
            itemPage = itemRepository
                    .getItems(PageRequest.of(pageNr, pageSize));
        }
        else{
            itemPage = itemRepository
                    .getItemsByPrice(minPrice, maxPrice, PageRequest.of(pageNr, pageSize));
        }
        Page<ItemDTO> copyPage = itemPage.map(ItemDTO::new);

        return new PageImpl<>(copyPage.getContent(), copyPage.getPageable(), copyPage.getTotalElements());




    }

    public ItemDTO saveItem(ItemDTO itemDTO, User user){
        Item item = new Item();
        item.setTitle(itemDTO.getTitle());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setLongitude(itemDTO.getLongitude());
        item.setLatitude(itemDTO.getLatitude());
        item.setImageUrls(itemDTO.getImageUrls());
        item.setUser(user);
        itemRepository.save(item);
        return new ItemDTO(item);
    }

}
