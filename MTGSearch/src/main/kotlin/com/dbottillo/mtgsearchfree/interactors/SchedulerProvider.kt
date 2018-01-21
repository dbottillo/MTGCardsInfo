package com.dbottillo.mtgsearchfree.interactors

import io.reactivex.Scheduler

interface SchedulerProvider{
    fun ui(): Scheduler
    fun io(): Scheduler
}