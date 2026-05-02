package com.creditpro.app.fragments

import android.os.Bundle
import androidx.navigation.NavDirections
import com.creditpro.app.R
import kotlin.Int

public class TipsFragmentDirections private constructor() {
  private data class ActionTipsToTipDetail(
    public val tipId: Int,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_tips_to_tipDetail

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putInt("tipId", this.tipId)
        return result
      }
  }

  public companion object {
    public fun actionTipsToTipDetail(tipId: Int): NavDirections = ActionTipsToTipDetail(tipId)
  }
}
