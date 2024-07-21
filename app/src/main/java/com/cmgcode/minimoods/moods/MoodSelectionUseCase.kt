package com.cmgcode.minimoods.moods

import com.cmgcode.minimoods.data.Mood
import com.cmgcode.minimoods.data.MoodRepository
import java.util.Date
import javax.inject.Inject

fun interface MoodSelectionUseCase {
    suspend operator fun invoke(date: Date, moodScore: Int)
}

class MoodSelectionUseCaseImpl @Inject constructor(
    private val repo: MoodRepository,
) : MoodSelectionUseCase {
    override suspend operator fun invoke(date: Date, moodScore: Int) {
        if (repo.getMoodForDay(date)?.mood == moodScore) {
            repo.removeMood(date)
        } else {
            repo.addMood(Mood(date, moodScore))
        }
    }
}