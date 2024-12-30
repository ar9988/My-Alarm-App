package com.example.myalarmapp.repository


import android.app.Activity
import android.content.Context
import com.example.myalarmapp.data.model.health.Day
import com.example.myalarmapp.data.model.health.DeviceFilterEnum
import com.example.myalarmapp.data.model.health.Session
import com.example.myalarmapp.data.model.health.SleepData
import com.example.myalarmapp.data.model.health.SleepStage
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.entries.SleepSession
import com.samsung.android.sdk.health.data.device.DeviceGroup
import com.samsung.android.sdk.health.data.device.DeviceType
import com.samsung.android.sdk.health.data.error.HealthDataException
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.ReadDataRequest
import com.samsung.android.sdk.health.data.request.ReadSourceFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject

class HealthDataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val healthDataStore by lazy { HealthDataService.getStore(context) }

    suspend fun requestSleepPermissions(activity: Activity): Boolean {
        val permissionSet = preparePermissionSet()

        var grantedPermission = healthDataStore.getGrantedPermissions(permissionSet)

        return if (grantedPermission.containsAll(permissionSet)) {
            true
        } else {
            runCatching {
                grantedPermission = healthDataStore.requestPermissions(permissionSet, activity)
            }.onFailure {
                if (it is HealthDataException) {
                    throw it
                } else {
                    it.printStackTrace()
                }
            }
            grantedPermission.containsAll(permissionSet)
        }
    }

    suspend fun readSleep(day: Day, device: DeviceFilterEnum): List<SleepData> {
        runCatching {
            val localTimeFilter =
                LocalTimeFilter.of(day.time.atStartOfDay(), day.time.plusDays(1).atStartOfDay())
            val deviceType: DeviceType? = when (device) {
                DeviceFilterEnum.WATCH -> DeviceGroup.WATCH
                DeviceFilterEnum.RING -> DeviceGroup.RING
                DeviceFilterEnum.ALL_DEVICES -> null
            }
            var readSourceFilter: ReadSourceFilter? = null
            deviceType?.let {
                readSourceFilter = ReadSourceFilter.of(
                    null,
                    deviceType
                )
            }
            val readRequest = prepareReadSleepRequest(localTimeFilter, readSourceFilter)
            val healthDataList = healthDataStore.readData(readRequest).dataList
            val sleepResults = healthDataList.map { healthData -> prepareSleepResult(healthData) }

            return sleepResults
        }.onFailure {
            throw it
        }

        return emptyList()
    }

    @Suppress("JoinDeclarationAndAssignment")
    private fun prepareSleepResult(healthData: HealthDataPoint): SleepData {
        healthData.let {
            val score: Int
            score = prepareSleepScore(it) ?: 0
            val duration = it.getValue(DataType.SleepType.DURATION) ?: Duration.ZERO
            val sleepSessionList = it.getValue(DataType.SleepType.SESSIONS) ?: emptyList()

            return SleepData(
                score,
                sleepSessionList.size,
                duration.toHours().toInt(),
                duration.minusHours(duration.toHours()).toMinutes().toInt(),
                it.startTime,
                it.endTime,
                extractSessions(sleepSessionList)
            )
        }
    }

    private fun extractSessions(
        sleepSessionList: List<SleepSession>
    ) = sleepSessionList.map {
        Session(
            "Session",
            it.duration.toHours(),
            it.duration.minusHours(it.duration.toHours()).toMinutes(),
            it.startTime,
            it.endTime,
            extractStages(it.stages ?: emptyList())
        )
    }

    private fun extractStages(
        stagesList: List<SleepSession.SleepStage>
    ) = stagesList.map { stage ->
        SleepStage(
            stage.startTime,
            stage.endTime,
            stage.stage.name.lowercase().replaceFirstChar { it.uppercaseChar() }
        )
    }


    /******************************************************************************************
     * [Practice 1] Prepare permission set to receive sleep data
     *
     * ----------------------------------------------------------------------------------------
     *
     * Use the provided variable 'sleepPermission' which is currently null and replace "TODO 1"
     * Create a Permission object of type 'DataTypes.SLEEP' and of 'AccessType.READ' and
     * assign it to 'sleepPermission' variable
     ******************************************************************************************/
    fun preparePermissionSet(): MutableSet<Permission> {
        val permissionSet: MutableSet<Permission> = mutableSetOf()
        var sleepPermission: Permission? = Permission.of(DataTypes.SLEEP, AccessType.READ)
        sleepPermission?.let {
            permissionSet.add(sleepPermission)
        }
        return permissionSet
    }

    /******************************************************************************************
     * [Practice 2] Prepare a read request for sleep data
     *
     * ----------------------------------------------------------------------------------------
     *
     * Use the provided variable 'localTimeFilter' and replace "TODO 2"
     * Set a time filter by calling '.setLocalTimeFilter(localTimeFilter)' on 'readRequestBuilder'
     ******************************************************************************************/
    fun prepareReadSleepRequest(
        localTimeFilter: LocalTimeFilter,
        readSourceFilter: ReadSourceFilter?
    ): ReadDataRequest<HealthDataPoint> {
        val readRequestBuilder = DataTypes.SLEEP.readDataRequestBuilder.setLocalTimeFilter(localTimeFilter)
        readSourceFilter?.let {
            readRequestBuilder.setSourceFilter(readSourceFilter)
        }
        return readRequestBuilder.build()
    }

    /******************************************************************************************
     * [Practice 3] Extract a sleep score from the Sleep Data
     *
     * ----------------------------------------------------------------------------------------
     *
     * Obtain a sleep score from health data point by calling 'healthDataPoint.getValue()'
     * and passing 'DataType.SleepType.SLEEP_SCORE' as an argument
     * Assign the obtained sleep score to 'sleepScore' variable
     ******************************************************************************************/
    fun prepareSleepScore(healthDataPoint: HealthDataPoint): Int? {
        var sleepScore: Int? = healthDataPoint.getValue(DataType.SleepType.SLEEP_SCORE)
        return sleepScore
    }
}
