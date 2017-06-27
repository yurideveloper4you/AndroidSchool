package ru.gdgkazan.simpleweather.screen.weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.gdgkazan.simpleweather.model.City;
import ru.gdgkazan.simpleweather.network.ApiFactory;

/**
 * @author Artur Vasilov
 */
public class RetrofitWeatherLoader extends Loader<City> {

    private final Call<City> mCall;

    @Nullable
    private City mCity;

    public RetrofitWeatherLoader(Context context, @NonNull String cityName) {
        super(context);
        mCall = ApiFactory.getWeatherService().getWeather(cityName);
    }

   @Override
    protected void onStartLoading() {
        //        мы проверяем, завершился ли запрос (доступны ли сохраненные данные). Если
        //        данные есть, то мы сразу возвращаем их, иначе начинаем загружать все заново
        super.onStartLoading();
        if (mCity != null) {
        //         deliverResult возвращает данные в LoaderCallbacks
            deliverResult(mCity);
        } else {
        //        forceLoad инициирует вызов метода onForceLoad
        //  По сути, этот метод служит только для удобства и
        // логического разделения между методами жизненного цикла и методами для
        //  загрузки данных
            forceLoad();

        }
    }

    @Override
    //            В методе onForceLoad вы должны загрузить данные асинхронно
    //            и вернуть результат с помощью метода deliverResult
    protected void onForceLoad() {
        super.onForceLoad();
        mCall.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                mCity = response.body();
                deliverResult(mCity);
            }

            @Override
            
            public void onFailure(Call<City> call, Throwable t) {
                deliverResult(null);
            }
        });
    }

    @Override
    //    мы должны остановить загрузку данных. Благо, с Retrofit это очень просто
    protected void onStopLoading() {
        mCall.cancel();
        super.onStopLoading();
    }
}

