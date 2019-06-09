package com.dbottillo.mtgsearchfree.dagger

import android.app.Application
import com.dbottillo.mtgsearchfree.MTGApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    CoreModule::class,
    NetworkModule::class,
    FragmentBuilder::class,
    InteractorsModule::class,
    DataModule::class,
    ActivityBuilder::class,
    AboutModule::class,
    ReleaseNoteModule::class])
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(app: MTGApp)

    override fun inject(instance: DaggerApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}