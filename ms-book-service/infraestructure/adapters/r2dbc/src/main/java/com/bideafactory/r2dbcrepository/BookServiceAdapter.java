package com.bideafactory.r2dbcrepository;

import java.text.DateFormat;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import com.bideafactory.model.dto.BookModel;
import com.bideafactory.model.exeptions.GenericException;
import com.bideafactory.model.gateways.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceAdapter implements BookService{
    private final DatabaseClient client;
    private final DateFormat formatter;

    @Override
    public Mono<BookModel> validateIsAvailable(BookModel model) {
        String query = "select b.id from schbook.book b"+
        " where ((to_timestamp(:start_date,'YYYY-MM-DD') between b.start_date and b.end_date)"+
        " or (to_timestamp(:end_date,'YYYY-MM-DD') between b.start_date and b.end_date))"+
        " and b.house_id = (select h.id from schbook.house h where h.house_id = :houseid)";

        return client.sql(query)
            .bind("houseid", model.getHouseId())
            .bind("start_date", formatter.format(model.getStartDate()))
            .bind("end_date", formatter.format(model.getEndDate()))
            .fetch().all().count()
            .flatMap(l -> (l>0)
                        ? Mono.error(GenericException.badRequest("The house is reserved on the required date."))
                        : Mono.just(model)
            );

    }

    @Override
    public Mono<BookModel> save(BookModel model) {
        String query = "insert into schbook.book(user_id,start_date,end_date,house_id,discount_code)"+
            "values(:user_id,to_timestamp(:start_date,'YYYY-MM-DD'),to_timestamp(:end_date,'YYYY-MM-DD')"
            +",(select id from schbook.house where house_id = :house_id),:discount)";
            
        return Mono.just(model)
            .flatMap(this::saveHouse)
            .flatMap(this::saveUser)
            .flatMap(m -> client.sql(query)
                    .bind("user_id", m.getId())
                    .bind("start_date", formatter.format(m.getStartDate()))
                    .bind("end_date", formatter.format(m.getEndDate()))
                    .bind("house_id", m.getHouseId())
                    .bind("discount", m.getDiscountCode())
                    .fetch().rowsUpdated()
                    .flatMap(i -> Mono.just(model))
            ).doOnError(e -> log.error("Error saving book in database: ",e))
            .onErrorMap(e -> GenericException.serverError("Error saving book in database",e))
            .doOnSuccess(m -> log.info("Book saved successfully..."));
    }

    protected Mono<BookModel> saveUser(BookModel model){
        return client.sql(
                "insert into schbook.user(id,name,lastname,age,phone_number) "+
                    "values(:id,:name,:lastname,:age,:phone) on conflict do nothing"
            ).bind("id", model.getId())
            .bind("name", model.getName())
            .bind("lastname", model.getLastname())
            .bind("age", model.getAge())
            .bind("phone", model.getPhoneNumber())
            .fetch().rowsUpdated()
            .map(j ->  model);
    }

    protected Mono<BookModel> saveHouse(BookModel model){
        String query = "insert into schbook.house(house_id) select :houseid from schbook.book "
            +"where not exists (select id from schbook.house where house_id = :houseid );";
        return client.sql(query)
            .bind("houseid", model.getHouseId())
            .fetch().rowsUpdated()
            .map(j -> model);
    }
}
