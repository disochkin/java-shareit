package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyDate;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemFrontDto itemToFrontItemDto(List<CommentNestedDto> commentNestedDtos, Item item) {
        ItemFrontDto itemFrontDto = new ItemFrontDto();
        itemFrontDto.setId(item.getId());
        itemFrontDto.setName(item.getName());
        itemFrontDto.setDescription(item.getDescription());
        itemFrontDto.setAvailable(item.isAvailable());
        itemFrontDto.setComments(commentNestedDtos);
        return itemFrontDto;
    }

    public static ItemNestedDto itemToItemNestedDto(Item item) {
        return new ItemNestedDto(
                item.getId(),
                item.getName()
        );
    }

    public static ItemFrontDtoWithBookingDate itemToFrontDtoWithBookingDate(BookingDtoOnlyDate lastBooking,
                                                                            BookingDtoOnlyDate nextBooking,
                                                                            List<CommentNestedDto> commentNestedDto,
                                                                            Item item) {
        ItemFrontDtoWithBookingDate itemFrontDtoWithBookingDate = new ItemFrontDtoWithBookingDate();
        itemFrontDtoWithBookingDate.setId(item.getId());
        itemFrontDtoWithBookingDate.setName(item.getName());
        itemFrontDtoWithBookingDate.setDescription(item.getDescription());
        itemFrontDtoWithBookingDate.setAvailable(item.isAvailable());
        itemFrontDtoWithBookingDate.setComment(commentNestedDto);
        itemFrontDtoWithBookingDate.setLastBooking(lastBooking);
        itemFrontDtoWithBookingDate.setNextBooking(nextBooking);
        return itemFrontDtoWithBookingDate;
    }

    public static Item itemAddDtoToItem(ItemAddDto itemAddDto) {
        Item item = new Item();
        item.setName(itemAddDto.getName());
        item.setDescription(itemAddDto.getDescription());
        item.setAvailable(itemAddDto.getAvailable());
        return item;
    }

    public static ItemNestedRequestDto itemToItemNestedRequestDto(Item item) {
        ItemNestedRequestDto itemNestedRequestDto = new ItemNestedRequestDto();
        itemNestedRequestDto.setId(item.getId());
        itemNestedRequestDto.setName(item.getName());
        itemNestedRequestDto.setIdOwner(item.getOwner().getId());
        return itemNestedRequestDto;
    }

}